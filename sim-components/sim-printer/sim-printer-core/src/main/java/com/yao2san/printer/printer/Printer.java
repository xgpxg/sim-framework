package com.yao2san.printer.printer;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.yao2san.printer.utils.FileTypeEnum;
import com.yao2san.printer.utils.FileTypeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;

import javax.imageio.ImageIO;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.Sides;
import java.awt.image.BufferedImage;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统打印服务
 *
 * @author wxg
 */
@Slf4j
public class Printer {

    private final static String HOME_DIR = System.getProperty("user.home") + File.separator + "printer" + File.separator;

    private final static String TEMP_FILE_PATH = HOME_DIR + "temp" + File.separator;

    private final static String PRINT_FILE_PATH = HOME_DIR + "files" + File.separator;

    static {
        File home = new File(TEMP_FILE_PATH);
        if (!home.exists()) {
            home.mkdir();
        }
    }

    /**
     * 获取指定打印机的所有属性
     *
     * @param printerName 打印机名称
     */
    public static Map<String, String> getPrinterAllAttributes(String printerName) {
        return getPrinterAttributes(printerName, null);
    }

    /**
     * 获取指定打印机的常用属性
     *
     * @param printerName 打印机名称
     */
    public static Map<String, String> getPrinterAttributes(String printerName) {
        List<String> defaultKeys = new ArrayList<>();
        defaultKeys.add("Name");
        //状态(暂未用到)
        defaultKeys.add("PrinterState");
        //状态 4 正在打印 3 空闲
        defaultKeys.add("PrinterStatus");
        //是否离线
        defaultKeys.add("WorkOffline");
        //是否是默认打印机
        defaultKeys.add("Default");
        //如果为True,表示打印机能支持一个队列中同时包含多个打印作业
        defaultKeys.add("Queued");
        //打印机支持的能力
        defaultKeys.add("CapabilityDescriptions");
        //当前打印任务数量
        defaultKeys.add("JobCountSinceLastReset");

        return getPrinterAttributes(printerName, defaultKeys);
    }

    /**
     * 调用windows驱动尽可能的获取打印机信息
     *
     * @param printerName 打印机名称
     */
    public static Map<String, String> getPrinterAttributes(String printerName, List<String> attributeKeys) {
        Map<String, String> infoMap = new HashMap<>(8);

        String command = "get-wmiobject -class win32_printer | Select-Object  ";
        if (attributeKeys != null && attributeKeys.size() > 0) {
            command += String.join(",", attributeKeys);
        } else {
            command += "*";
        }
        command += " | where {$_.Name -eq '" + printerName + "'}";

        log.debug(command);

        ProcessBuilder builder = new ProcessBuilder("powershell.exe", command);

        String info = null;
        Process reg;
        builder.redirectErrorStream(true);
        try {
            reg = builder.start();
            info = new BufferedReader(new InputStreamReader(reg.getInputStream(), "gbk"))
                    .lines().collect(Collectors.joining(System.lineSeparator()));
            reg.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (info == null) {
            log.warn("活动打印机信息为空");
            return infoMap;
        }
        String[] split = info.split(System.lineSeparator());
        for (String s : split) {
            if (StringUtils.isEmpty(s)) {
                continue;
            }
            String key = s.split(":")[0].trim();
            String value = s.split(":")[1].trim();
            infoMap.put(key, value);
        }
        log.debug(info);

        return infoMap;
    }

    /**
     * 获取默认打印机名称
     */
    public static String getDefaultPrinterName() {
        return PrintServiceLookup.lookupDefaultPrintService().getName();
    }

    /**
     * 获取默认打印机常用属性
     */
    public static Map<String, String> getDefaluePrinterAttribute() {
        String defaultPrinterName = getDefaultPrinterName();
        return getPrinterAttributes(defaultPrinterName);
    }

    /**
     * 获取所有打印机名称
     */
    public static List<String> getAllPrinterName() {
        List<String> names = new ArrayList<>();
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        if (printServices != null && printServices.length > 0) {
            for (PrintService printService : printServices) {
                names.add(printService.getName());
            }
        }
        return names;
    }

    /**
     * 获取所有打印机常用属性
     */
    public static Map<String, Map<String, String>> getAllPrinterAttribute() {
        Map<String, Map<String, String>> attrMap = new HashMap<>(8);
        List<String> names = getAllPrinterName();
        for (String name : names) {
            attrMap.put(name, getPrinterAttributes(name));
        }
        return attrMap;
    }

    public static void print(InputStream inputStream) throws Exception {
        print(inputStream, getDefaultPrinterName());
    }

    /**
     * stream打印 自动识别文件类型(支持PDF、DOX、DOCX、PNG、JPG、GIF自动识别)
     *
     * @param inputStream 文件流
     * @param printerName 打印机名称
     */
    public static void print(InputStream inputStream, String printerName) throws Exception {
        String tempFileName = TEMP_FILE_PATH + UUID.randomUUID();
        try {
            write2File(inputStream, tempFileName);

            FileTypeEnum type = FileTypeUtil.getType(new FileInputStream(tempFileName));
            if (type == null) {
                log.error("Not support file type: null");
            }
            switch (type) {
                case PDF:
                    printPdf(tempFileName, printerName);
                    break;
                case DOC:
                case DOCX:
                    printWord(tempFileName, printerName);
                    break;
                case PNG:
                case JPG:
                case GIF:
                    printImage(tempFileName, printerName);
                    break;
                default:
                    log.error("Not support file type: {}", type.name);
            }

        } finally {
            inputStream.close();
        }

    }

    /**
     * 打印WORD
     *
     * @param filePath    文件路径
     * @param printerName 打印机名称
     */
    public static void printWord(String filePath, String printerName) {
        ComThread.InitSTA();

        ActiveXComponent wd = new ActiveXComponent("Word.Application");

        Dispatch.put(wd, "Visible", new Variant(false));
        Dispatch document = wd.getProperty("Documents").toDispatch();
        Dispatch doc = Dispatch.invoke(document, "Open", Dispatch.Method,
                new Object[]{filePath}, new int[1]).toDispatch();
        try {
            wd.setProperty("ActivePrinter", new Variant(printerName));
            Dispatch.call(doc, "PrintOut").toJavaObject();
        } finally {
            Dispatch.call(doc, "Close", new Variant(false));
            wd.invoke("Quit", new Variant[]{});
            ComThread.Release();
            ComThread.quitMainSTA();
        }
    }

    /**
     * 使用流打印word文件
     *
     * @param inputStream 文件流
     * @param printerName 打印机名称
     */
    public static void printWord(InputStream inputStream, String printerName) throws Exception {
        printWordToPdf(inputStream, printerName);
    }

    private static void printWordToPdf(InputStream inputStream, String printerName) throws Exception {
        String tempPath = TEMP_FILE_PATH;
        String wordTempFile = tempPath + UUID.randomUUID() + ".word.temp";
        String pdfTempFile = tempPath + UUID.randomUUID() + ".pdf.temp";
        try {
            write2File(inputStream, wordTempFile);
            word2Pdf(wordTempFile, pdfTempFile);
            printPdf(pdfTempFile, printerName);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private static void write2File(InputStream inputStream, String distFilePath) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(distFilePath)) {
            int index;
            byte[] bytes = new byte[1024];
            while ((index = inputStream.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, index);
                fileOutputStream.flush();
            }
        }
    }

    private static void printWordToPdf(String filePath, String printerName) throws Exception {
        String tempPath = TEMP_FILE_PATH;
        String tempFile = tempPath + UUID.randomUUID();
        word2Pdf(filePath, tempFile);
        printPdf(tempFile, printerName);
    }

    @SuppressWarnings("all")
    private static void word2Pdf(String filePath, String distFilePath) {
        ComThread.InitSTA();

        ActiveXComponent wd = new ActiveXComponent("Word.Application");

        Dispatch.put(wd, "Visible", new Variant(false));
        Dispatch document = wd.getProperty("Documents").toDispatch();
        Dispatch doc = Dispatch.invoke(document, "Open", Dispatch.Method,
                new Object[]{filePath}, new int[1]).toDispatch();
        Dispatch.call(doc, "SaveAs", distFilePath, 17).toJavaObject();
        wd.invoke("Quit", new Variant[]{});
        ComThread.Release();
        ComThread.quitMainSTA();
    }


    /**
     * 打印PDF
     *
     * @param filePath    文件路径
     * @param printerName 打印机名称
     */
    public static void printPdf(String filePath, String printerName) throws Exception {
        File file = new File(filePath);
        printPdf(new FileInputStream(file), printerName);
    }

    /**
     * 使用流打印pdf
     *
     * @param inputStream 文件流
     * @param printerName 打印机名称
     */
    public static void printPdf(InputStream inputStream, String printerName) throws Exception {
        PDDocument document = null;
        try {
            document = PDDocument.load(inputStream);
            PrinterJob printJob = PrinterJob.getPrinterJob();
            printJob.setJobName(String.valueOf(UUID.randomUUID()));
            if (printerName != null) {
                PrintService[] printServices = PrinterJob.lookupPrintServices();
                if (printServices == null || printServices.length == 0) {
                    log.error("Not found any printer");
                    return;
                }
                PrintService printService = null;
                for (PrintService service : printServices) {
                    System.out.println(service.getName());
                    if (service.getName().contains(printerName)) {
                        printService = service;
                        break;
                    }
                }
                if (printService != null) {
                    printJob.setPrintService(printService);
                } else {
                    log.error("Not found printer: {}", printerName);
                    return;
                }
            }

            PDFPrintable pdfPrintable = new PDFPrintable(document, Scaling.ACTUAL_SIZE);

            Book book = new Book();
            PageFormat pageFormat = new PageFormat();
            //纵向
            pageFormat.setOrientation(PageFormat.PORTRAIT);

            book.append(pdfPrintable, pageFormat, document.getNumberOfPages());

            printJob.setPageable(book);
            printJob.setCopies(1);
            //打印属性
            HashPrintRequestAttributeSet pars = new HashPrintRequestAttributeSet();
            //单双页
            pars.add(Sides.ONE_SIDED);

            printJob.print(pars);

            log.debug("Printing...");

        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 图片打印
     *
     * @param filePath    文件路径
     * @param printerName 打印机名称
     */
    public static void printImage(String filePath, String printerName) throws Exception {
        PDDocument pdDocument = new PDDocument();
        File file = new File(filePath);
        BufferedImage image = ImageIO.read(new FileInputStream(file));

        PDPage pdPage = new PDPage(new PDRectangle(image.getWidth(), image.getHeight()));
        pdDocument.addPage(pdPage);
        PDImageXObject pdImageXObject = LosslessFactory.createFromImage(pdDocument, image);
        PDPageContentStream contentStream = new PDPageContentStream(pdDocument, pdPage);
        contentStream.drawImage(pdImageXObject, 0, 0, image.getWidth(), image.getHeight());
        contentStream.close();
        String tempFile = TEMP_FILE_PATH + UUID.randomUUID() + ".image.temp";
        pdDocument.save(tempFile);
        pdDocument.close();

        printPdf(tempFile, printerName);
    }

    /**
     * 打印excel
     *
     * @param filePath    文件路径
     * @param printerName 打印机名称
     */
    public static void printExcel(String filePath, String printerName) throws Exception {
        String tempFile = TEMP_FILE_PATH + UUID.randomUUID() + ".pdf";

        excel2Pdf(filePath, tempFile);
        printPdf(tempFile, printerName);


    }

    /**
     * excel转pdf
     *
     * @param filePath     excel文件
     * @param distFilePath 输出文件
     */
    public static void excel2Pdf(String filePath, String distFilePath) {
        ComThread.InitSTA();

        ActiveXComponent wd = new ActiveXComponent("Excel.Application");

        Dispatch.put(wd, "Visible", new Variant(false));
        Dispatch document = wd.getProperty("Workbooks").toDispatch();
        Dispatch doc = Dispatch.invoke(document, "Open", Dispatch.Method,
                new Object[]{filePath,
                        new Variant(false),
                        new Variant(false)}, new int[1]).toDispatch();
        try {
            Dispatch.invoke(doc, "SaveAs", Dispatch.Method, new Object[]{
                    distFilePath, new Variant(57), new Variant(true),
                    new Variant(57), new Variant(57),
                    new Variant(true), new Variant(true),
                    new Variant(57), new Variant(true),
                    new Variant(true), new Variant(true)}, new int[1]);
        } finally {
            Dispatch.call(doc, "Close", new Variant(false));
            wd.invoke("Quit", new Variant[]{});
            ComThread.Release();
            ComThread.quitMainSTA();
        }
    }

    /**
     * 清理临时文件
     */
    public static void cleanTempFile() {
        File delFile = new File(TEMP_FILE_PATH);
        if (delFile.isDirectory()) {
            for (File file : delFile.listFiles()) {
                FileUtils.deleteQuietly(file);
            }
        }
    }

}
