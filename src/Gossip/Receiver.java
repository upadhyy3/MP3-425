package Gossip;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.log4j.Logger;
 
 
public class Receiver implements Runnable {

// Logger	
public static final Logger LOGGER = Logger.getLogger(Receiver.class);
 
//    private volatile boolean running;
    private AtomicBoolean running = new AtomicBoolean();
    private Member self;
    private DatagramSocket server;
     
    Receiver(Member self)
    {
        this.running.set(true);
        this.self = self;
        try {
            server = new DatagramSocket(this.self.getport());
        } catch (SocketException e) {
            e.printStackTrace();
        }
         
    }
    public void terminate()
    {
    	
    	this.running.set(false);
    	System.out.println("Receiver Thread Stopped");
    	System.out.println(running.get());
    }
    public long idParser(String parser)     
    {
        String[] parts = parser.split(":");
        String ip = parts[0];
        long timeStamp = Long.valueOf(parts[1]).longValue();
        return timeStamp;
    }
     
     
     
    public void MergeWithRemote(List<Member> receivedList) throws InterruptedException
    {
         
                for(Member received: receivedList)
                { 
                	
                	
                	if(application.activeNodes.contains(received)) 
            		{
            			Member localMember = application.activeNodes.get(application.activeNodes.indexOf(received));
            			if(received.getHeartBeat() > localMember.getHeartBeat())
                		{
                		localMember.setHeartBeat(received.getHeartBeat());
                		localMember.setTimestamp();
				localMember.setId(received.getId());
//                		System.out.println("Heartbeat updated");
                		
                		LOGGER.info("This is change in HeartBeat " + String.valueOf(localMember.getHeartBeat()));
                        	LOGGER.info("This is change in Timestamp" + String.valueOf(localMember.getTimestamp()));
                		}
            		}
                	else
                	 	{
                		    if(application.deadlist.contains(received))
                		    {
                		    	Member localDeadMember = application.deadlist.get(application.deadlist.indexOf(received));
                    			if(received.getHeartBeat() > localDeadMember.getHeartBeat())
                        		{	application.deadlist.remove(localDeadMember);
                        		
                        			LOGGER.info("A Member has been removed from the deadList" + localDeadMember); 
                        		
                    				Member resurrectedLocalMember = new Member(received.getAddress(),received.getport(),received.getHeartBeat(),received.getId(),received.getTimestamp(),received.getMarkedFail(),received.getMarkedDeleted());
                    				resurrectedLocalMember.setTimestamp();
						application.activeNodes.add(resurrectedLocalMember);
                    				LOGGER.info("A Dead Member has been added to the List again" + resurrectedLocalMember); 
                    				
                    				
//                        		System.out.println("Heartbeat updated");
                        		}
                		    }
                		    else{
                	 		Member newLocalMember = new Member(received.getAddress(),received.getport(),received.getHeartBeat(),received.getId(),received.getTimestamp(),received.getMarkedFail(),received.getMarkedDeleted());
//                	 		System.out.println("New Member added");
                	 		//System.out.println(newLocalMember.getAddress());
                	 		application.activeNodes.add(newLocalMember);
                	 		newLocalMember.setTimestamp();
                	 		
                            		LOGGER.info("A New Member has been added to the List" + newLocalMember); 
                            	//	newLocalMember.setTimestamp();
                           
                            		LOGGER.info("A new Timestamp has been set for New Member" + String.valueOf(newLocalMember.getTimestamp()));
                	 		
                	 		}
                 		}  
                }            
           }
     //       }

    // extract the member arraylist out of the packet
    
    @Override
    public void run() {
       
        while(running.get())
        {
        try {

            byte[] buf = new byte[1024];
            DatagramPacket p = new DatagramPacket(buf, buf.length);
            server.receive(p);
 
            // extract the member arraylist out of the packet
            // TODO: maybe abstract this out to pass just the bytes needed
            ByteArrayInputStream bais = new ByteArrayInputStream(p.getData());
            ObjectInputStream ois = new ObjectInputStream(bais);
 
            Object readObject = ois.readObject();
            if(readObject instanceof List<?>) {
                List<Member> list = (List<Member>) readObject;
            //    ArrayList<Member> list = (List<Member>) readObject;
 
//                System.out.println("Received member list:");
                for (Member member : list)
                {
                    String id = member.getId();
                    //long timeStampAtReceiver = long(timeStamp);	// printing various attributes of the list
//                    System.out.println(member.getAddress());
//                    System.out.println(member.getId());
//                    System.out.println(member.getHeartBeat());
                   



 // Calling the merge method to update the local list with new heart beat and Id values
                     
                }
                MergeWithRemote(list);
             
                //Thread.sleep(1000);
            }
        } catch (IOException e) 
        {
            e.printStackTrace();
            this.running.set(false);
        } catch (ClassNotFoundException e) 
        {
            e.printStackTrace();
            this.running.set(false);
        }
        catch(InterruptedException ie)
        {
                 
            ie.printStackTrace();
            this.running.set(false);
        }
     
        }
    }
} 
