package trabajofinalalgoritmica2;

import java.util.ArrayList;
/**
 *
 * @author Fernando Fuentes Perez
 */
public class Doctor implements Cloneable{
    private final int NUMEROMAXIMOPACIENTES = 8;
    private final int SUELDO = 1000;
    private ArrayList<Patient> listaPacientes;
    private Residence domicilio;
    private final int codMedico;
    
    public Doctor(Residence residence, int codMedico){
        this.listaPacientes = new ArrayList();
        this.domicilio = residence;
        this.codMedico = codMedico;
    }
    public Doctor(Doctor d){
        this.listaPacientes = new ArrayList();
        this.domicilio = d.getDomicilio();
        this.codMedico = d.getCodMedico();
    }

    public ArrayList<Patient> getListaPacientes() {
        return listaPacientes;
    }

    public void setListaPacientes(ArrayList<Patient> listaPacientes) {
        this.listaPacientes = listaPacientes;
    }

    public Residence getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(Residence domicilio) {
        this.domicilio = domicilio;
    }
    
    public int getCodMedico(){
        return this.codMedico;
    }
    
    public int getNumMaxPacientes(){
        return this.NUMEROMAXIMOPACIENTES;
    }
    
    public int getSueldo(){
        return this.SUELDO;
    }
    
    public boolean puedeAsignarNuevoPaciente(){
        boolean res = false;
        if(listaPacientes.size()<NUMEROMAXIMOPACIENTES){
            res = true;
        }
        return res;
    }
    
    public String muestraListaPacientes(){
        String res = "";
        for(int i=0; i<listaPacientes.size(); i++){
            res += " "+this.listaPacientes.get(i).getCodPatient()+" ";
        }
        return res;
    }
    
    public boolean equals(Object obj){
        if ( obj == null ) return false;
        if ( this == obj ) return true;
        if ( ! (obj instanceof Doctor ) ) return false;
        Doctor d = (Doctor) obj;
        return this.codMedico == d.getCodMedico() ;
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
