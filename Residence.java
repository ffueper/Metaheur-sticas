package trabajofinalalgoritmica2;
/**
 *
 * @author Fernando Fuentes Perez
 */
public class Residence {

    private int x;
    private int y;

    public Residence(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double calcularDistanciaDesde(Residence parametros) {
        double cateto1 = x - parametros.getX();
        double cateto2 = y - parametros.getY();
        double hipotenusa = Math.sqrt(cateto1 * cateto1 + cateto2 * cateto2);
        return hipotenusa;
    }

    public String toString() {
        return "(x = " + getX() + ", y = " + getY() + " )";
    }
}