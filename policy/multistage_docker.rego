package main
import rego.v1
# Asegurarse de que se use multistage para build
deny contains msg if {
    not es_multistage
    msg := "Dockerfile no usa multistage para build (no hay varios FROM)"
}
# Hay más de un FROM con stage
es_multistage if {
    count([ 1 |
        some i
        lower(input.dockerfile[0][i].Cmd) == "from"
    ]) > 1
}