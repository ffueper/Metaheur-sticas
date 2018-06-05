package trabajofinalalgoritmica2;
/**
 *
 * @author Fernando Fuentes Perez
 */
public class Patient implements Cloneable{
    private final int codPatient;
    private Residence address;
    
    public Patient(Residence residence, int codPatient){
        this.codPatient = codPatient;
        this.address = residence;
        
    }

    public Residence getAddress() {
        return address;
    }

    public void setAddress(Residence address) {
        this.address = address;
    }
    
    public int getCodPatient(){
        return this.codPatient;
    }
    
    public Object clone() {
        Object clone = null;
        try {
            clone = super.clone();
        } catch (CloneNotSupportedException e) {
            // No deberia suceder
        }
        return clone;
    }
    
}
