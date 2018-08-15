package polimi.awt.model;

import com.google.gson.annotations.Expose;

import javax.persistence.*;

//Class to store the alternative name (different languages) of a Peak
// clase qeus e utiliza para base en AlternativePeak...  que son alternativas de nombres y anotaciones para PEAKS
@MappedSuperclass
public class PeakNameBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    private long id;

    @Expose
    private String name;

    @Column(name = "lang", length = 10)
    @Expose
    private String lang;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

}