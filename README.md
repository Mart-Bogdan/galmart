# Galactic Markets

Galactic Markets is a utility mod that allows to check shortages and surpluses, as well as low and high prices of any commodity. Additionally, it allows to track this information in the form of side intel.

![Demo](https://i.imgur.com/6rzWGiL.png)

## Building project

Execute following command, it would produce zip file inside `build/` dir.

Linux/Mac:
```shell
./gradlew release
```

Windows
```cmd
gradlew.bat release
```

You might need to configure gradle to use JDK 7 or 8.
It might not compile on latest JDK due to unsupported target of 1.7.

It worked for me from IDE: Intelij IDEA with JDK 11.
