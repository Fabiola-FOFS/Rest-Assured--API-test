# API Automation Tests - Restful Booker

Este projeto contém testes automatizados de API para o site [Restful-Booking](https://restful-booker.herokuapp.com/) utilizando **Rest Assured**, **JUnit 5** e **Maven**.

## 📌 Tecnologias Utilizadas

- **Java 21**
- **Rest Assured** - Biblioteca para testes de API REST
- **JUnit 5** - Framework para execução de testes
- **Maven** - Gerenciador de dependências e build
- **Allure** - Geração de relatórios de testes

## 🛠️ Configuração do Ambiente

### 🔹 Pré-requisitos
Antes de executar os testes, certifique-se de ter instalado:

- **Java JDK 21** ([Download](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html))
- **Maven** ([Download](https://maven.apache.org/download.cgi))
- **Allure** ([Guia de Instalação](https://docs.qameta.io/allure/#_installing_a_commandline))

Verifique as versões instaladas executando:
```sh
java -version
mvn -version
allure --version
```

### 🔹 Clonando o Repositório
```sh
git clone https://github.com/seu-usuario/seu-repositorio.git
cd seu-repositorio
```

### 🔹 Instalando Dependências
```sh
mvn clean install
```

## 🚀 Executando os Testes

### 🔸 Rodar todos os testes
```sh
mvn test
```

### 🔸 Gerar e abrir o relatório Allure
```sh
allure serve target/allure-results/
```

## 📂 Estrutura do Projeto
```
Rest-Assured--API-test/
│-- src/
│   ├── main/       # (Caso tenha classes auxiliares)
│   ├── test/       # Testes automatizados
│-- pom.xml         # Configuração do Maven
│-- README.md       # Documentação do projeto
```

## 🔌 Plugins e Dependências
As principais dependências utilizadas estão no `pom.xml`:
```xml
<dependencies>
    <dependency>
        <groupId>io.rest-assured</groupId>
        <artifactId>rest-assured</artifactId>
        <version>4.5.1</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-engine</artifactId>
        <version>5.9.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>io.qameta.allure</groupId>
        <artifactId>allure-junit5</artifactId>
        <version>2.19.0</version>
    </dependency>
</dependencies>
```

## 📄 Licença
Este projeto é de código aberto e está sob a licença [MIT](LICENSE).
