package main
import rego.v1
# No usar tag latest
deny contains msg if {
    some i
    lower(input.dockerfile[0][i].Cmd) == "from"
    endswith(lower(input.dockerfile[0][i].Value), ":latest")
    msg := "No sede usar tag 'latest' en la instruccción FROM"
}

# Dockerfile debe exponer al menos un puerto
deny contains msg if {
    not existe_expose
    msg := "El Dockerfile no expone nungún puero (no tiene EXPOSE)"
}
# Hay al menos un EXPOSE
existe_expose if {
    some i
    lower(input.dockerfile[0][i].Cmd) == "expose"
}