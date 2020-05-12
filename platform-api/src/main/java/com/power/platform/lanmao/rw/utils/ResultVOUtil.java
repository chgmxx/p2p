package com.power.platform.lanmao.rw.utils;

/**
 * 
 * @author pm
 *
 */ 
public class ResultVOUtil {
	
	public static ResultVO success(Object object) {
        ResultVO resultVO = new ResultVO();
        resultVO.setData(object);
        resultVO.setCode(0);
        resultVO.setMsg("成功");
        return resultVO;
    }
	public static ResultVO fail(Object object) {
		ResultVO resultVO = new ResultVO();
        resultVO.setData(object);
        resultVO.setCode(1);
        resultVO.setMsg("失败");
        return resultVO;
	}
}
