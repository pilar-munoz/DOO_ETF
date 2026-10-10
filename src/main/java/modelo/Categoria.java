package modelo;

public class Categoria {
    private int id;
    private final String nombre;

    // id 0 = cat nueva (asigna la BD)
    public Categoria(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
}
