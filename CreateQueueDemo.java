package com.aliyun.mns.sample.Queue;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.common.utils.ServiceSettings;
import com.aliyun.mns.model.Message;
import com.aliyun.mns.model.QueueMeta;


public class CreateQueueDemo {
    static Frame contain=new Frame("Aliyun queue");
    static Panel board=new Panel();
    static Label l1=new Label("send data");
    static TextField st=new TextField();
    static Button sb=new Button("send");
    static Button fb=new Button("get");
    static Button cr=new Button("create");
    static Button de=new Button("delete");
    static CloudAccount account = new CloudAccount(
            ServiceSettings.getMNSAccessKeyId(),
            ServiceSettings.getMNSAccessKeySecret(),
            ServiceSettings.getMNSAccountEndpoint());
    static MNSClient client = account.getMNSClient(); //this client need only initialize once
	public static void createWindow()
	{	
		//进程通信界面构建
		contain.setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 5));
        board.add(l1);
        board.add(st);
        board.add(sb);
        board.add(fb);
        board.add(cr);
        board.add(de);
        contain.add(board);
        contain.pack(); 
        contain.setVisible(true);
    	contain.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	client.close();
            	System.exit(0);
            }
            });
	}
	public static void addCreatListener()
	{	
	       cr.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {  
	                try
	                {   //Create Queue
	                    QueueMeta qMeta = new QueueMeta();
	                    qMeta.setQueueName("cloud-queue-demo");
	                    qMeta.setPollingWaitSeconds(30);//use long polling when queue is empty.
	                    CloudQueue cQueue = client.createQueue(qMeta);
	                    System.out.println("Create queue successfully. URL: " + cQueue.getQueueURL());
	                } catch (ClientException ce)
	                {
	                    System.out.println("Something wrong with the network connection between client and MNS service."
	                            + "Please check your network and DNS availablity.");
	                    ce.printStackTrace();
	                } catch (ServiceException se)
	                {
	                    if (se.getErrorCode().equals("QueueNotExist"))
	                    {
	                        System.out.println("Queue is not exist.Please create before use");
	                    } else if (se.getErrorCode().equals("TimeExpired"))
	                    {
	                        System.out.println("The request is time expired. Please check your local machine timeclock");
	                    }
	                    /*
	                    you can get more MNS service error code in following link.
	                    https://help.aliyun.com/document_detail/mns/api_reference/error_code/error_code.html?spm=5176.docmns/api_reference/error_code/error_response
	                    */
	                    se.printStackTrace();
	                } catch (Exception e1)
	                {
	                    System.out.println("Unknown exception happened!");
	                    e1.printStackTrace();
	                }

	            }
	            });
	}
	public static void addDeletListener()
	{	
	       de.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {       
            	try
            	{   //Delete Queue
            		CloudQueue queue = client.getQueueRef("cloud-queue-demo");
            		queue.delete();           
            		System.out.println("Delete cloud-queue-demo successfully!");
            	}catch (ClientException ce)
            	{
            		System.out.println("Something wrong with the network connection between client and MNS service."
            				+ "Please check your network and DNS availablity.");
            		ce.printStackTrace();
            	} catch (ServiceException se)
            	{
            		if (se.getErrorCode().equals("QueueNotExist"))
            		{
            			System.out.println("Queue is not exist.Please create before use");
            		} else if (se.getErrorCode().equals("TimeExpired"))
            		{
            			System.out.println("The request is time expired. Please check your local machine timeclock");
            		}
            		/*
            		 * you can get more MNS service error code in following link.
            		 * https://help.aliyun.com/document_detail/mns/api_reference/error_code/error_code.html?spm=5176.docmns/api_reference/error_code/error_response
            	     */
            		se.printStackTrace();
            	} catch (Exception e1)
            	{
            		System.out.println("Unknown exception happened!");
            		e1.printStackTrace();
            	}

            }
        	});
	}
	public static void addProduceListener()
	{	
        sb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try{
                    CloudQueue queue = client.getQueueRef("cloud-queue-demo");// replace with your queue name
                    Message message = new Message();
                    String s=st.getText();
                    message.setMessageBody("demo_message_body" + s); // use your own message body here
                    Message putMsg = queue.putMessage(message);
                    System.out.println("Send message id is: " + putMsg.getMessageId());
                } catch (ClientException ce)
                {
                    System.out.println("Something wrong with the network connection between client and MNS service."
                            + "Please check your network and DNS availablity.");
                    ce.printStackTrace();
                } catch (ServiceException se)
                {
                    if (se.getErrorCode().equals("QueueNotExist"))
                    {
                        System.out.println("Queue is not exist.Please create before use");
                    } else if (se.getErrorCode().equals("TimeExpired"))
                    {
                        System.out.println("The request is time expired. Please check your local machine timeclock");
                    }
                    /*
                    you can get more MNS service error code from following link:
                    https://help.aliyun.com/document_detail/mns/api_reference/error_code/error_code.html?spm=5176.docmns/api_reference/error_code/error_response
                    */
                    se.printStackTrace();
                } catch (Exception e1)
                {
                    System.out.println("Unknown exception happened!");
                    e1.printStackTrace();
                }
            }
            });
	}
	public static void addConsumeListener()
	{	
        fb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try{
                    CloudQueue queue = client.getQueueRef("cloud-queue-demo");// replace with your queue name
                        Message popMsg = queue.popMessage();
                        if (popMsg != null){
                            System.out.println("message handle: " + popMsg.getReceiptHandle());
                            System.out.println("message body: " + popMsg.getMessageBodyAsString());
                            System.out.println("message id: " + popMsg.getMessageId());
                            System.out.println("message dequeue count:" + popMsg.getDequeueCount());
                            //<<to add your special logic.>>                           
                            //remember to  delete message when consume message successfully.
                            queue.deleteMessage(popMsg.getReceiptHandle());
                            System.out.println("delete message successfully.\n");
                    }
                } catch (ClientException ce)
                {
                    System.out.println("Something wrong with the network connection between client and MNS service."
                            + "Please check your network and DNS availablity.");
                    ce.printStackTrace();
                } catch (ServiceException se)
                {
                    if (se.getErrorCode().equals("QueueNotExist"))
                    {
                        System.out.println("Queue is not exist.Please create queue before use");
                    } else if (se.getErrorCode().equals("TimeExpired"))
                    {
                        System.out.println("The request is time expired. Please check your local machine timeclock");
                    }
                    /*
                    you can get more MNS service error code in following link.
                    https://help.aliyun.com/document_detail/mns/api_reference/error_code/error_code.html?spm=5176.docmns/api_reference/error_code/error_response
                    */
                    se.printStackTrace();
                } catch (Exception e1)
                {
                    System.out.println("Unknown exception happened!");
                    e1.printStackTrace();
                }
            }
            });
	}
	public static void addWindowFunction()
	{	
		addCreatListener();
		addProduceListener();
		addConsumeListener();
		addDeletListener();
	}
}

