public class NewJavaClass {
  private String name;
  NewJavaClass(){
  }
  NewJavaClass(String name){
	this.name = name; 
  }
	
  // Getter
  public String getName() {
    return name;
  }

  // Setter
  public void setName(String newName) {
    this.name = newName;
  }
}