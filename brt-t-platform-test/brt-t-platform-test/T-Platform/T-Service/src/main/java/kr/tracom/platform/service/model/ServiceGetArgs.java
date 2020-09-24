package kr.tracom.platform.service.model;

import kr.tracom.platform.tcp.model.TcpChannelMessage;
import lombok.Data;

import java.util.concurrent.BlockingQueue;

@Data
public class ServiceGetArgs {
    private BlockingQueue<TcpChannelMessage> readQueue;
}
