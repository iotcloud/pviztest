package cgl.pviz.test;

import cgl.pviz.rpc.*;
import cgl.pviz.rpc.Message;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class ActiveMQSender {
    private boolean run = true;

    public void stop() {
        run = false;
    }

    public void run() {
        try {
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://149.165.159.16:61616");

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // Create the destination (Topic or Queue)
            Destination destination = session.createTopic("topic2");
            // Create a MessageProducer from the Session to the Topic or Queue
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // Create a messages

            while (run) {


                Message.PvizMessage m;
                Message.PvizMessage.Builder build = Message.PvizMessage.newBuilder();
                build.setStepid(3);
                build.setTimestamp(System.currentTimeMillis());
                build.setType(Message.PvizMessage.mtype.DATA);

                for (int i = 0; i < 100; i++) {
                    build.addLabels(i);
                    double x = Math.random() * 100;
                    double y = Math.random() * 100;
                    double z = Math.random() * 100;
                    build.addPositions(Message.Position.newBuilder().setX(x).setY(y).setZ(z));
                }
                m = build.build();
                BytesMessage message = session.createBytesMessage();
                message.writeBytes(m.toByteArray());
                producer.send(message);
            }
            // Clean up
            session.close();
            connection.close();
        }
        catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ActiveMQSender sender = new ActiveMQSender();
        sender.run();
    }
}
