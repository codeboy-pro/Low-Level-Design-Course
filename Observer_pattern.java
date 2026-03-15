import java.util.ArrayList;
import java.util.List;
interface ISubscriber   {
  public void update();
}
interface IChannel{
  public void Subscribe(ISubscriber sub);
  public void unsubscribe(ISubscriber sub);
  public void notifySubscribers();
}
class Channel implements IChannel{
  private String name;
  private String latestVideo;

  private List<ISubscriber>subscribers;
  public Channel(String name){
    this.name=name;
    this.subscribers=new ArrayList<>();
  }
  @Override 
  public void Subscribe(ISubscriber sub){
    if(!subscribers.contains(sub)){
      subscribers.add(sub);
    }
  }
  @Override
  public void unsubscribe(ISubscriber sub){
    subscribers.remove(sub);
  }
  
  @Override
  public void notifySubscribers() {
    // TODO Auto-generated method stub
    for(ISubscriber sub:subscribers){
      sub.update();
    }
  }
  public void uploadVideo(String title){
    latestVideo=title;
      System.out.println("\n[" + name + " uploaded \"" + title + "\"]");
        notifySubscribers();
  }
  public String getVideo(){
    return "\nCheckout our new Video : " + latestVideo + "\n";
  }
}

class Subscriber implements ISubscriber{
  private String name;
 private Channel channel;
 public Subscriber(String name,Channel channel){
  this.name=name;
  this.channel=channel;
 }
 @Override
 public void update(){
  System.out.println("Hey " + name + "," + channel.getVideo());
 }

}
public class Observer_pattern {
public static void main(String[] args) {
  Channel channel=new Channel("CoderArmy");
  Subscriber sub1=new Subscriber("Pradip", channel);
  Subscriber sub2=new Subscriber("Amit", channel);
  channel.Subscribe(sub1);
  channel.Subscribe(sub2);
  channel.uploadVideo("Observer DEsign pattern");
  channel.unsubscribe(sub2);
  channel.uploadVideo("Decorate Design Pattern");

}
  
}