package com.tec.bufeo.capitan.Activity.DetallesTorneo.EquiposDtorneo.Repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.tec.bufeo.capitan.Activity.DetallesTorneo.EquiposDtorneo.Models.EquiposTorneo;

import java.util.List;

@Dao
public interface EquiposTorneoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(EquiposTorneo resultModel);

    /*@Query("SELECT * from equipos WHERE mi_equipo = :mi_equipo")
    LiveData<List<EquiposTorneo>> getAllEquipos(String mi_equipo);*/

    @Query("SELECT * from equipostorneo WHERE equipo_torneo = :equipo_torneo ")
    LiveData<List<EquiposTorneo>> getAllEquiposTorneo(String equipo_torneo);


    @Query("DELETE FROM equipostorneo")
    void deleteAll();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEquipo(List<EquiposTorneo> resultModel);

}
