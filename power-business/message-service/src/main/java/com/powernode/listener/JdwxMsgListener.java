package com.powernode.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.powernode.constant.QueueConstant;
import com.powernode.service.SmsLogService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

@Component
public class JdwxMsgListener {

    @Autowired
    private SmsLogService smsLogService;


    @RabbitListener(queues = QueueConstant.PHONE_CODE_QUEUE)
    public void handlerMsg(Message message, Channel channel) {
        //获取消息
        HashMap map = JSON.parseObject(new String(message.getBody()), HashMap.class);
        //调用短信接口
//        String result = HttpUtil.post("https://way.jd.com/chuangxin/dxjk", map);
        String resultJson = "{\n" +
                "    \"code\": \"10000\",\n" +
                "    \"charge\": false,\n" +
                "    \"remain\": 1305,\n" +
                "    \"msg\": \"查询成功\",\n" +
                "    \"result\": {\n" +
                "        \"ReturnStatus\": \"Success\",\n" +
                "        \"Message\": \"ok\",\n" +
                "        \"RemainPoint\": 420842,\n" +
                "        \"TaskID\": 18424321,\n" +
                "        \"SuccessCounts\": 1\n" +
                "    }\n" +
                "}";
        //将json格式的字符串转换为json对象
        JSONObject jsonObject = JSONObject.parseObject(resultJson);
        //获取通信标识
        String code = jsonObject.getString("code");
        if ("10000".equals(code)) {
            //获取业务处理结果
            String returnStatus = jsonObject.getJSONObject("result").getString("ReturnStatus");
            if ("Success".equals(returnStatus)) {
                //业务处理成功，并记录短信内容
                smsLogService.saveJdwxMsg(map);
            }
        }
        try {
            //签收消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
