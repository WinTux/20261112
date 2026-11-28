package main
import rego.v1
deny contains msg if {
    some i
    lower(input.dockerfile[0][i].Cmd) == "from"
    contains(lower(input.dockerfile[0][i].Value), "jdk-8")
    msg := "No se permite usar JDK 8"
}
deny contains msg if {
    some i
    lower(input.dockerfile[0][i].Cmd) == "from"
    contains(lower(input.dockerfile[0][i].Value), "jdk-11")
    msg := "No se permite usar JDK 11"
}
deny contains msg if {
    some i
    lower(input.dockerfile[0][i].Cmd) == "from"
    contains(lower(input.dockerfile[0][i].Value), "jdk-17")
    msg := "No se permite usar JDK 17"
}
deny contains msg if {
    some i
    lower(input.dockerfile[0][i].Cmd) == "from"
    contains(lower(input.dockerfile[0][i].Value), "jdk-21")
    msg := sprintf("No se permite usar JDK 21, se detectó %v", [input.dockerfile[0][i].Value])
}