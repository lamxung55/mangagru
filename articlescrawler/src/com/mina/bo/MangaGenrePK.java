/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mina.bo;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Mina Mimi
 */
@Embeddable
public class MangaGenrePK implements Serializable {

    @Basic(optional = false)
    @Column(name = "genre_id")
    private int genreId;
    @Basic(optional = false)
    @Column(name = "manga_id")
    private long mangaId;

    public MangaGenrePK() {
    }

    public MangaGenrePK(int genreId, long mangaId) {
        this.genreId = genreId;
        this.mangaId = mangaId;
    }

    public int getGenreId() {
        return genreId;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    public long getMangaId() {
        return mangaId;
    }

    public void setMangaId(long mangaId) {
        this.mangaId = mangaId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) genreId;
        hash += (int) mangaId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MangaGenrePK)) {
            return false;
        }
        MangaGenrePK other = (MangaGenrePK) object;
        if (this.genreId != other.genreId) {
            return false;
        }
        if (this.mangaId != other.mangaId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mina.bo.MangaGenrePK[ genreId=" + genreId + ", mangaId=" + mangaId + " ]";
    }
    
}
