/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mina.bo;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Mina Mimi
 */
@Entity
@Table(name = "manga_genre")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MangaGenre.findAll", query = "SELECT m FROM MangaGenre m")
    , @NamedQuery(name = "MangaGenre.findByGenreId", query = "SELECT m FROM MangaGenre m WHERE m.mangaGenrePK.genreId = :genreId")
    , @NamedQuery(name = "MangaGenre.findByMangaId", query = "SELECT m FROM MangaGenre m WHERE m.mangaGenrePK.mangaId = :mangaId")})
public class MangaGenre implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected MangaGenrePK mangaGenrePK;

    public MangaGenre() {
    }

    public MangaGenre(MangaGenrePK mangaGenrePK) {
        this.mangaGenrePK = mangaGenrePK;
    }

    public MangaGenre(int genreId, long mangaId) {
        this.mangaGenrePK = new MangaGenrePK(genreId, mangaId);
    }

    public MangaGenrePK getMangaGenrePK() {
        return mangaGenrePK;
    }

    public void setMangaGenrePK(MangaGenrePK mangaGenrePK) {
        this.mangaGenrePK = mangaGenrePK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (mangaGenrePK != null ? mangaGenrePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MangaGenre)) {
            return false;
        }
        MangaGenre other = (MangaGenre) object;
        if ((this.mangaGenrePK == null && other.mangaGenrePK != null) || (this.mangaGenrePK != null && !this.mangaGenrePK.equals(other.mangaGenrePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mina.bo.MangaGenre[ mangaGenrePK=" + mangaGenrePK + " ]";
    }
    
}
