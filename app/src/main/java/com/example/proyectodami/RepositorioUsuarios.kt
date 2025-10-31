package com.example.proyectodami

import com.example.proyectodami.data.entities.Usuario

object RepositorioUsuarios {
    val listaUsuarios = mutableListOf<Usuario>()

    fun registrarUsuario(usuario: Usuario): Boolean {
        if (listaUsuarios.any { it.correo == usuario.correo }) {
            return false
        }
        listaUsuarios.add(usuario)
        return true
    }

    fun validarLogin(correo: String, contrasena: String): Boolean {
        return listaUsuarios.any { it.correo == correo && it.contrasena == contrasena }
    }
}
