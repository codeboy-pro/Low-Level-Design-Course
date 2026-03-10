#include<iostream>
using namespace std;
class walkable{
public:
virtual void walk()=0;
virtual ~walkable(){}
};
class NormalWalk:public walkable{
public:
void walk() override{
    cout<<"waalking Normally"<<endl;
}
};
class NoWalk:public walkable{
  public:
  void walk() override{
    cout<<"No walk..."<<endl;
  }
};
class Talkable{
public:
virtual void talk()=0;
virtual ~Talkable(){}
};
class Nomaltalk:public Talkable{
public:
void talk() override{
  cout<<"Normal talk..."<<endl;
}

};
class Notalk:public Talkable{
 public:
 void talk() override{
  cout<<"No talk..."<<endl;
 }
};
class Flyable{
public:
  virtual void fly()=0;
  virtual ~Flyable(){}
};
class NormalFly:public Flyable{
  public:
  void fly() override{
     cout<<"Nomal fly..."<<endl;
  }
};
class Nofly:public Flyable{
  public:
  void fly() override{
    cout<<"No fly.."<<endl;
  }
};
class Robot{
  protected:
walkable* w;
Talkable* t;
Flyable* f;
public:
Robot(walkable* w,Talkable* t,Flyable* f){
  this->w=w;
  this->t=t;
  this->f=f;
}

void walk(){
  w->walk();
}
void talk(){
  t->talk();
}
void fly(){
  f->fly();
}
virtual void projection()=0;
};
class CompanionR:public Robot{
  public:
  CompanionR(walkable* w,Talkable* t,Flyable* f):Robot(w,t,f){}
  void projection() override{
cout<<"displaying friendly companion features..."<<endl;
  }
};
class WorkerR : public Robot {
public:
    WorkerR(walkable* w, Talkable* t, Flyable* f)
        : Robot(w, t, f) {}

    void projection() override {
        cout << "Displaying worker efficiency stats..." << endl;
    }
};
int main(){
Robot* R=new CompanionR(new NormalWalk(),new Nomaltalk(),new Nofly);
R->walk();
R->talk();
R->fly();
R->projection();
Robot *r=new WorkerR(new NoWalk(),new Nomaltalk(),new NormalFly());
r->talk();
r->walk();
r->projection();
  return 0;
}