package main
import rego.v1
# Validar que los puertos sean correctos
deny contains msg if {
    some i
    lower(input.dockerfile[0][i].Cmd) == "expose"
    not puerto_valido(lower(input.dockerfile[0][i].Value))
    msg := sprintf("El puerto %v no es válido, solo se permiten 7070 y 7071", [input.dockerfile[0][i].Value])
}

# Puerto válido 7070
puerto_valido(p) if {
    p == "7070"
}

puerto_valido(p) if {
    p == "7071"
}