package Gossip;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import Leader.ElectionTCPServer;



public class application {

//    public static ArrayList<Member> activeNodes;
    public static List<Member> activeNodes;
    public static List<Member> deadlist;   
    public  Member self;
   // public boolean MainFlag;
    private AtomicBoolean mainFlag = new AtomicBoolean();
    //String selfIp;
    public static String introducerIp;
    public static int introducerPort;
    public static String myIpAddress;
    public static int myPort;
    public static String leaderIP;
   
    public application() throws UnknownHostException
    {
    	this.mainFlag.set(true);
        this.introducerIp = "172.22.151.24";
        this.leaderIP = "172.22.151.30";
        this.myIpAddress =  InetAddress.getLocalHost().getHostAddress().toString();   
        this.introducerPort = 2000;
        this.myPort = 2000;
       
    }
   
    public void terminate()
    {
    	mainFlag.set(false);
    	System.out.println("Checker Thread Stopped");
    	}
    
    public Member getSelf() {
        return self;
    }

    public void setSelf(Member self) {
        this.self = self;
    }
   
    public void print()
    {
    	for(int i =0 ;i<application.activeNodes.size();i++)
    	{
    		String id = activeNodes.get(i).getId();
    		String status = Integer.toString(activeNodes.get(i).getHeartBeat());
    		String format = "%-40s%s%n";
    		System.out.printf(format,"ID(IP:TimeStamp","CurrentHeartBeat");
    		System.out.printf(format,id,status);
    	}
    }
   
    public void iniateSenderReceiver() throws InterruptedException
    {
    	
        Receiver receiver = new Receiver(this.self);
        Thread receiverThread = new Thread(receiver);
    //  receiver thread started
        Sender sender = new Sender(this.self);

        Thread senderThread = new Thread(sender);
  //     sender thread started
        
        Checker checker = new Checker(this.self);
        Thread checkerThread = new Thread(checker);
   //    checker thread started
        
    	ElectionTCPServer ets = new ElectionTCPServer();
    	ets.start();
 
        while(this.mainFlag.get())
        {
        	System.out.println("Welcome!! What do you want to with the application");
        	System.out.println("Press number corresponding to key");
        	System.out.println("1 See the current membership list");
        	System.out.println("2 See the seld id");
        	System.out.println("3 join the group");
        	System.out.println("4 Voluntarily Leave the group");
        	int userInput;
        	Scanner a = new Scanner(System.in);
        	userInput=a.nextInt();
        	Thread.sleep(200);
        	switch(userInput)
        	{
        	case 1 :				print();
        							continue;
        	case 2 :				System.out.printf("%-40s%s%n","Self Id:",activeNodes.get(application.activeNodes.indexOf(this.self)).getId());
        							continue;
        	case 3 :				receiverThread.start();
        							senderThread.start();
        							checkerThread.start();
        							
        							continue;
        	case 4 :         		System.out.println(checkerThread.isAlive());
    								checker.terminate();
    								checkerThread.join();
    								System.out.println("$$");
    								System.out.println(checkerThread.isAlive());
    								System.out.println("$$");
    								sender.terminate();
    								senderThread.join();
    								receiver.terminate();
    								receiverThread.join();
    								terminate();
    								ets.stop();
    								
    								break;
    								
    								
    		default:				System.out.println("You are fired !! It's not a correct option");
    								continue;
        	}
        	
        	System.exit(0);

        	
        	//return;
        }

       
    }
   
    public void startup()
    {

        activeNodes = Collections.synchronizedList((new ArrayList<Member>()));
        //Checks whether the Member is the Introducer or Not
        deadlist = Collections.synchronizedList((new ArrayList<Member>()));
        if(introducerIp.compareTo(myIpAddress)==0)
        {
            System.out.println("Initialised");
           
            this.self = new Member(myIpAddress,myPort,0,myIpAddress+":"+System.currentTimeMillis(),System.currentTimeMillis(), false, false);
            activeNodes.add(this.self);
           
        }
        else
        {
            System.out.println("Initialised Both");
            this.self = new Member(myIpAddress,myPort,0,myIpAddress+":"+System.currentTimeMillis(),System.currentTimeMillis(), false, false);
            activeNodes.add(this.self);
            Member Introducer = new Member(introducerIp,introducerPort,0,introducerIp+":"+System.currentTimeMillis(), System.currentTimeMillis(), false, false);
            activeNodes.add(Introducer);
        }
       
    }
   
    public static void main(String [] args)
    {
        application ap;
        System.out.println(" Enter the Choices ");
        try {
            ap = new application();
            ap.startup();
            ap.iniateSenderReceiver();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
			e.printStackTrace();
		}
        

    }


   
}
