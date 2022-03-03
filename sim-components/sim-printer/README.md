打印组件,完善中...

功能：

- 支持WORD、PDF、图片、EXCEL打印

- 使用Java实现



| API                                                                                 |
| -------------------------------------------------------------------------------------------------- |
| cleanTempFile()                                                                                    |
| 清理临时文件                                                                                 |
| excel2Pdf(java.lang.String filePath, java.lang.String distFilePath)                                |
| excel转pdf                                                                                        |
| getAllPrinterAttribute()                                                                           |
| 获取所有打印机常用属性                                                                  |
| getAllPrinterName()                                                                                |
| 获取所有打印机名称                                                                        |
| getDefaluePrinterAttribute()                                                                       |
| 获取默认打印机常用属性                                                                  |
| getDefaultPrinterName()                                                                            |
| 获取默认打印机名称                                                                        |
| getPrinterAllAttributes(java.lang.String printerName)                                              |
| 获取指定打印机的所有属性                                                               |
| getPrinterAttributes(java.lang.String printerName)                                                 |
| 获取指定打印机的常用属性                                                               |
| getPrinterAttributes(java.lang.String printerName, java.util.List<java.lang.String> attributeKeys) |
| 调用windows驱动尽可能的获取打印机信息                                               |
| print(java.io.InputStream inputStream)                                                             |
| print(java.io.InputStream inputStream, java.lang.String printerName)                               |
| stream打印 自动识别文件类型(支持PDF、DOX、DOCX、PNG、JPG、GIF自动识别)        |
| printExcel(java.lang.String filePath, java.lang.String printerName)                                |
| 打印excel                                                                                        |
| printImage(java.lang.String filePath, java.lang.String printerName)                                |
| 图片打印                                                                                       |
| printPdf(java.io.InputStream inputStream, java.lang.String printerName)                            |
| 使用流打印pdf                                                                                 |
| printPdf(java.lang.String filePath, java.lang.String printerName)                                  |
| 打印PDF                                                                                          |
| printWord(java.io.InputStream inputStream, java.lang.String printerName)                           |
| 使用流打印word文件                                                                          |
| printWord(java.lang.String filePath, java.lang.String printerName)                                 |
| 打印WORD



> 注意：仅支持在windows，且需安装office