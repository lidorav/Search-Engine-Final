package Model.PartA;

public class PostTerm {
    private String name;
    private int df;
    private int tf;
    private int ptr;


    public PostTerm(PreTerm preTerm){
        this.name = preTerm.getName();
        this.tf = preTerm.getTf();
        this.df = 1;
    }

    public PostTerm(String name, String df, String tf, String ptr){
        this.name = name;
        this.df = Integer.valueOf(df);
        this.tf = Integer.valueOf(tf);
        this.ptr = Integer.valueOf(ptr);
    }

    public void increaseTf(int tf){
        this.tf +=tf;
    }

    public void increaseDf(){
        this.df++;
    }

    public void setPtr(int ptr){
        this.ptr = ptr;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getDf() {
        return df;
    }

    public int getTf() {
        return tf;
    }

    public int getPtr() {
        return ptr;
    }

    @Override
    public String toString() {
        return  name + ":" + df + "," + tf + "," + ptr;
    }
}
