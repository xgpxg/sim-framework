package com.yao2san.busi.sql.bean;

import com.yao2san.sim.framework.web.response.ResponseData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("sqlFragment")
public class SqlFragmentController {
    @GetMapping
    public ResponseData qrySqlFragmentList(){
        return null;
    }
    @GetMapping("{sqlFragmentId}")
    public ResponseData qrySqlFragmentList(@PathVariable Long sqlFragmentId){
        return null;
    }
}
