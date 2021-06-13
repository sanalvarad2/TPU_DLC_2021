/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DLC.TPI.Clases;

/**
 *
 * @author Santiago
 */
import javax.persistence.*;
import DLC.TPI.DAO.Commons.IDALEntity;

@Entity
@Table(name = "ARCHIVOS")

@NamedQueries(
        {
            @NamedQuery(name = "Archivo.findAll", query = "SELECT a FROM Archivo a"),
            @NamedQuery(name = "Archivo.findById", query = "SELECT a FROM Archivo a WHERE a.idDocumento = :idDocumento")
        }
)
/**
 *
 * @author Santiago
 */
public class Archivo implements IDALEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    @Column(unique = true, name = "id_documento")
    private Integer idDocumento;

    @Column(unique = false, name = "file")
    private String file;

    public Archivo() {
    }

    public Archivo(Integer idDocumento) {
        this.idDocumento = idDocumento;
    }

    public Archivo(Integer idDocumento, String file) {
        this.idDocumento = idDocumento;
        this.file = file;
    }

    public void setIdDocumento(Integer id) {
        this.idDocumento = id;
    }

    public Integer getIdDocumento() {
        return this.idDocumento;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

}