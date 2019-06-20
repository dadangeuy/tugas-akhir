package its.pushnotification.service;

import its.pushnotification.external.Fcm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class QueueService {

	public static int thread_max=5;
	public static int thread_count=0;
	public static int counter=1;
	public static Thread[] thread_pool=new Thread[1000];
	public static int[] thread_status= new int[1000];
	public static int[] thread_active= new int[1000];
	public static LinkedList<Map<String,Object>> queue_list=new LinkedList<Map<String,Object>>();	
	
	public String put_work(ArrayList<String> userDeviceIdKey, String title, String message, String image)
	{
		//System.out.println("> Put the data into stack");
		Map<String,Object> work_block=new HashMap<String, Object>();
		work_block.put("userDeviceIdKey",userDeviceIdKey);
		work_block.put("title", title);
		work_block.put("message", message);
		work_block.put("image", image);
		this.enqueue(work_block);
		//System.out.println(work_block);
		
		return "> Putting the data into message queue stack";
	}
	
	public void threadpool_start(int threadNumber)
	{
		for (int i=0;i<threadNumber;i++)
		{
			Runnable r = new runthread(i);
			//System.out.println(i);
			thread_pool[i] =new Thread(r);
			thread_pool[i].start();
			thread_active[i]=1;
			thread_status[i]=0;
			thread_count++;
		}
		System.out.println("> Message queue started with "+ threadNumber +" thread");
	}
	
	public void clear_threadpool()
	{
		for (int i=0;i<thread_count; i++)
		{
			thread_active[i]=0;
			/*while (true)
			{
				if(thread_status[i]==0)
				{
					thread_pool[i].interrupt();
					break;
				}
			}*/
		}
		thread_count=0;
	}
	
	public int add_thread()
	{
		Runnable r = new runthread(thread_count);
		thread_pool[thread_count] = new Thread(r);
		thread_pool[thread_count].start();
		thread_status[thread_count]=0;
		thread_count++;
		return thread_count;
	}
	
	public int remove_thread()
	{
		//thread_pool[thread_count-1].interrupt();
		thread_active[thread_count-1]=0;
		thread_count--;
		return thread_count+1;
	}
	
	
	public void enqueue(Map<String,Object> inputObject)
	{
		if(queue_list.size()==0)
		{
			queue_list.addFirst(inputObject);
		}
		else
		{
			queue_list.addLast(inputObject);
		}
	}
	
	public static Map<String,Object>dequeue()
	{
		//ArrayList<Map<String,Object>> container=new ArrayList<Map<String,Object>>();
		Map <String,Object> result =new HashMap<String,Object>();
		if(queue_list.size()>0)
		{
			result=queue_list.pollFirst();
		}
		return result;
	}
	
	/*public  void do_task()
	{
		ArrayList<Map<String,Object>> items=this.dequeue();
		for (Map <String,Object> item : items)
		{
			Runnable r = new runthread(item);
			Thread thread = new Thread(r);
			thread.start();
			thread.interrupt();
		}
	}*/
	
	class runthread implements Runnable
	{
		Map<String,Object> workData;
		int thread_number;
		public runthread(int number)
		{
			this.thread_number=number;
		}
		
		@Override
		public void run()
		{
			while (thread_active[thread_number]==1)
			{			
				workData=dequeue();
				if(workData!=null)
				if(workData.size()>0)
				{
					thread_status[thread_number]=1;
					//System.out.println("> Thread no "+this.thread_number+" is excecuting");
					
					//System.out.println(counter);
					counter++;
					
					Fcm fcmNotif = new Fcm();
					@SuppressWarnings("unchecked")
					ArrayList<String> userDeviceIdKey=(ArrayList<String>)workData.get("userDeviceIdKey");
					String title = (String)workData.get("title");
					String message = (String)workData.get("message");
					String image = (String)workData.get("image");
					try{
						System.out.println("> Thread no "+this.thread_number+" is excecuting");
						fcmNotif.pushFCMNotification(userDeviceIdKey, title, message, image);
						//System.out.println(result);
					}
					catch (Exception e){
						System.out.println (e);
					}
					thread_status[thread_number]=0;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//System.out.println("in queue scheduler");
			
		}
		
		
	}
	
	
	
}
