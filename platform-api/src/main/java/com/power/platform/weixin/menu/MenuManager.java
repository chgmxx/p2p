package com.power.platform.weixin.menu;

import org.springframework.stereotype.Component;

import com.power.platform.weixin.utils.WeixinUtil;

@Component
public class MenuManager {
    /**
     * 组装菜单数据
     * @return
     */
    public Menu getMenu() {
    	
    	// 第一栏菜单
        CommonButton btn11 = new CommonButton();
        btn11.setName("进入官网");
        btn11.setType("view");
        btn11.setUrl(WeixinUtil.weixinIndexUrl);
        System.out.println(btn11.getUrl());
        btn11.setKey("11");
        
        
        CommonButton btn23 = new CommonButton();
        btn23.setName("银行存管");
        btn23.setType("view");
        btn23.setUrl("http://u366680.viewer.maka.im/k/PHNY0DDF");
        btn23.setKey("23");
        
       /* CommonButton btn24 = new CommonButton();
        btn24.setName("尊贵体验官");
        btn24.setType("view");
        btn24.setUrl("http://form.mikecrm.com/f.php?t=b4Xfiv");
        btn24.setKey("24");*/
        
        // 第三栏菜单
        CommonButton btn30 = new CommonButton();
        btn30.setName("绑定/解绑");
        btn30.setType("view");
        btn30.setUrl(WeixinUtil.weixinBindUrl);
        btn30.setKey("30");
        
        CommonButton btn36 = new CommonButton();
        btn36.setName("招贤纳士");
        btn36.setType("view");
        btn36.setUrl("http://www.rabbitpre.com/m/BmzNQe62i?lc=3&sui=AzkGokmN#from=share");
        btn36.setKey("36");
        
        CommonButton btn31 = new CommonButton();
        btn31.setName("了解中投摩根");
        btn31.setType("view");
        btn31.setUrl("https://www.cicmorgan.com/more_disclosure.html");
        btn31.setKey("31");
        
        CommonButton btn33 = new CommonButton();
        btn33.setName("新年活动");
        btn33.setType("view");
        btn33.setUrl("https://www.cicmorgan.com/zt_newyear.html");  //需要调试
        btn33.setKey("33");
        
        /*CommonButton btn34 = new CommonButton();
        btn34.setName("新手指引");
        btn34.setType("view");
        btn34.setUrl("http://d.eqxiu.com/s/rV9YqCt1?eqrcode=1&from=singlemessage&isappinstalled=0");
        btn34.setKey("34");*/
        
        
        CommonButton btn35 = new CommonButton();
        btn35.setName("联系我们");
        btn35.setType("view");
        btn35.setUrl("http://mp.weixin.qq.com/s?__biz=MzAwMjI5NTcxMg==&mid=210998391&idx=1&sn=4b3f0290bdbae97eecc023706c66cfa7&scene=18#wechat_redirect");
        btn35.setKey("35");
        
        
        RootButton mainBtn1 = new RootButton();
        mainBtn1.setName("进入官网");
        mainBtn1.setSub_button(new CommonButton[] { btn11});


        RootButton mainBtn2 = new RootButton();
        mainBtn2.setName("银行存管");
        mainBtn2.setSub_button(new CommonButton[] { btn23 });


        RootButton mainBtn3 = new RootButton();
        mainBtn3.setName("关于我们");
        mainBtn3.setSub_button(new CommonButton[] {  btn33,btn30,btn36, btn31,btn35 });

        Menu menu = new Menu();
        menu.setButton(new Button[] { btn11, mainBtn2, mainBtn3 });

        return menu;
    }
}
