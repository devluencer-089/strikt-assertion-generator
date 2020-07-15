![CI](https://github.com/michaelom/strikt-assertion-generator/workflows/CI/badge.svg?event=push)
[![codecov](https://codecov.io/gh/michaelom/strikt-assertion-generator/branch/master/graph/badge.svg)](https://codecov.io/gh/michaelom/strikt-assertion-generator)

# strikt-assertion-generator
Generate boilerplate-free [strikt](strikt.io) assertions for Kotlin data classes

### Why would I need generated assertions?
Consider the following Kotlin data class:

```kotlin
data class Person(
    val name: String,
    val sex: Sex,
    val size: Int,
    val dateOfBirth: Instant,
    val car: Car,
    val child: Person? = null
)
```

A typical test asserting on instances of `Person` might look like this:

```kotlin
    val person = sut.doSomething(person)    

    expectThat(person) {
        get { name } isEqualTo "Hans"
        get { sex } isEqualTo Male
        get { size } isEqualTo 183
        get { dateOfBirth } isEqualTo date

        with(Person::car) {
            get { make } isEqualTo "Fiat"
            get { year } isEqualTo 1999
        }

        with(Person::child) {
            isNotNull().and {
                get { name } isEqualTo "Linda"
                get { sex } isEqualTo Female
                get { size } isEqualTo 170

                with(Person::child) {
                    isNotNull().and {
                        get { name } isEqualTo "Marie"
                        get { sex } isEqualTo Female
                        get { size } isEqualTo 155
                    }
                }

            }
        }
    }
```  
Let's take a look at this:  
The test assertions look quite good, but there is some noise around `get` and `with` functions that makes it a bit less pleasant to read.
What's more, it is sometimes hard for developers new to Strikt to discover and properly use functions like `with`, `get` and `and`. 
It needs some practice to write proper assertions particularly around nested and nullable properties.

Using Strikt Assertion Generator, it is possible to generate assertions specific to `Person`. The same test can be rewritten like this:

```kotlin
    val person = sut.doSomething(person)
    
    expectThat(person) {
        name isEqualTo "Hans"
        sex isEqualTo Male
        size isEqualTo 183
        dateOfBirth isEqualTo date
        car {
            make isEqualTo "Fiat"
            year isEqualTo 1999
        }
        child {
            name isEqualTo "Linda"
            sex isEqualTo Female
            size isEqualTo 170
            child {
                name isEqualTo "Marie"
                sex isEqualTo Female
                size isEqualTo 155
            }
        }
    }
```  

Voila! All the boilerplate around `get`, `with` and `isNotNull` is gone.

### Usage / getting started
#### 1. add dependencies to `build.gradle` 
```groovy
dependencies {
    compileOnly "com.michaelom.strikt.assertion-generator-annotation:$version"
    kapt "com.michaelom.strikt.assertion-generator:$version"
}
```
#### 2. add gradle plugin to `build.gradle`
```groovy
plugins {
    id 'com.michaelom.strikt.assertion-generator'
}
```
#### 3. annotate data classes with `@GenerateAssertions` 
```kotlin
@GenerateAssertions
data class Person(
    val name: String,
    val sex: Sex,
    val car: Car
)
```
#### 4. let `kapt` generate assertions
Trigger the `kaptKotlin` gradle task in your project (`./gradlew kaptKotlin`).  

If you now navigate to `${build}/generated/source/kaptKotlin/strikt` you will find the generated assertions 
for your annotated data classes.




## Contributing
//TODO


### TODO
- publish plugin
- publish annotation processor and annotation
- support for sequences and collections
- add more examples 

#### Debugging the annotation processor:

Run tests without a gradle daemon using the following command:
```shell script
./gradlew clean check --no-daemon -Dorg.gradle.debug=true -Dkotlin.compiler.execution.strategy="in-process" -Dkotlin.daemon.jvm.options="-Xdebug,-Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=n"
```

Then attach to the JVM on localhost port 5005 by using the following command line arguments:
```shell script
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
```
