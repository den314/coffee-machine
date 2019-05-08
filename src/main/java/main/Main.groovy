package main

import groovy.transform.ToString
import groovy.transform.builder.Builder

@Builder
class CoffeeRecipe {
    int millis
    int milkMillis
    Strength strength
    CoffeeType coffeeType
}

enum CoffeeType {
    CAPPUCCINO{
        @Override
        CoffeeRecipe getRecipe() {
            CoffeeRecipe.builder()
                    .millis(300)
                    .strength(Strength.TASTEFUL)
                    .milkMillis(100)
                    .coffeeType(CAPPUCCINO)
                    .build()
        }

        @Override
        Coffee create(CoffeeRecipe recipe) {
            new CreateCappuccinoCommand().createCoffee(recipe)
        }
    },
    LATTE{
        @Override
        CoffeeRecipe getRecipe() {
            CoffeeRecipe.builder()
                    .millis(350)
                    .strength(Strength.TASTEFUL)
                    .milkMillis(250)
                    .coffeeType(LATTE)
                    .build()
        }

        @Override
        Coffee create(CoffeeRecipe recipe) {
            new CreateLatteCommand().createCoffee(recipe)
        }
    },
    AMERICANO{
        @Override
        CoffeeRecipe getRecipe() {
            CoffeeRecipe.builder()
                    .millis(300)
                    .strength(Strength.STRONG)
                    .milkMillis(0)
                    .coffeeType(AMERICANO)
                    .build()
        }

        @Override
        Coffee create(CoffeeRecipe recipe) {
            new CreateAmericanoCommand().createCoffee(recipe)
        }
    },
    FLAT_WHITE{
        @Override
        CoffeeRecipe getRecipe() {
            CoffeeRecipe.builder()
                    .millis(300)
                    .strength(Strength.TASTEFUL)
                    .milkMillis(50)
                    .coffeeType(FLAT_WHITE)
                    .build()
        }

        @Override
        Coffee create(CoffeeRecipe recipe) {
            new CreateFlatWhiteCommand().createCoffee(recipe)
        }
    }

    abstract CoffeeRecipe getRecipe()

    abstract Coffee create(CoffeeRecipe recipe)

    Coffee prepare(CoffeeRecipe recipe) {
        switch (this) {
            case AMERICANO:
                return AMERICANO.create(recipe ?: AMERICANO.recipe)
            case FLAT_WHITE:
                return FLAT_WHITE.create(recipe ?: FLAT_WHITE.recipe)
            case CAPPUCCINO:
                return CAPPUCCINO.create(recipe ?: CAPPUCCINO.recipe)
            case LATTE:
                return LATTE.create(recipe ?: LATTE.recipe)
            default:
                throw new IllegalArgumentException("unknown coffee type")
        }
    }
}

interface CreateCoffeeCommand {
    Coffee createCoffee(CoffeeRecipe recipe)
}

class CreateFlatWhiteCommand implements CreateCoffeeCommand {

    @Override
    Coffee createCoffee(CoffeeRecipe recipe) {
        new FlatWhite(recipe.strength, recipe.milkMillis)
    }
}

class CreateAmericanoCommand implements CreateCoffeeCommand {

    @Override
    Coffee createCoffee(CoffeeRecipe recipe) {
        new Americano(recipe.strength)
    }
}

class CreateCappuccinoCommand implements CreateCoffeeCommand {

    @Override
    Coffee createCoffee(CoffeeRecipe recipe) {
        new Cappuccino(recipe.strength)
    }
}

class CreateLatteCommand implements CreateCoffeeCommand {

    @Override
    Coffee createCoffee(CoffeeRecipe recipe) {
        new Latte(recipe.strength)
    }
}

enum Strength {
    LIGHT, TASTEFUL, STRONG
}

class Coffee {
    Strength strength

    Coffee(Strength strength) {
        this.strength = strength
    }

    @Override
    String toString() {
        return "${getClass()}{strength=$strength}"
    }
}

class Cappuccino extends Coffee {
    Cappuccino(Strength strength) {
        super(strength)
    }
}

class Latte extends Coffee {
    Latte(Strength strength) {
        super(strength)
    }
}

class Americano extends Coffee {
    Americano(Strength strength) {
        super(strength)
    }
}

@ToString(includeSuperProperties = true)
class FlatWhite extends Coffee {
    double milkInLiter

    FlatWhite(Strength strength, double milkInLiter) {
        super(strength)
        this.milkInLiter = milkInLiter
    }
}

class CoffeeMachine {

    Coffee make(CoffeeType type) {
        type.prepare()
    }

    Coffee prepareCoffee(CoffeeRecipe coffeeRecipe) {
        coffeeRecipe.coffeeType.prepare(coffeeRecipe)
    }

    Coffee make(CoffeeRecipe userDefinedRecipe) {
        prepareCoffee(update(userDefinedRecipe))
    }

    private CoffeeRecipe update(CoffeeRecipe userRecipe) {
        def defaultRecipe = userRecipe.coffeeType.recipe
        CoffeeRecipe.builder()
                .coffeeType(userRecipe.coffeeType)
                .milkMillis(userRecipe.milkMillis ?: defaultRecipe.milkMillis)
                .millis(userRecipe.millis ?: defaultRecipe.millis)
                .strength(userRecipe.strength ?: defaultRecipe.strength)
                .build()
    }
}

coffeeMachine = new CoffeeMachine()
coffee = coffeeMachine.make(CoffeeType.FLAT_WHITE)

assert coffee.class == FlatWhite
assert coffee.strength == CoffeeType.FLAT_WHITE.recipe.strength

println coffee

customCoffee = coffeeMachine.make(
        CoffeeRecipe.builder()
                .coffeeType(CoffeeType.AMERICANO)
                .strength(Strength.LIGHT)
                .millis(500)
                .build())

assert customCoffee.class == Americano
assert customCoffee.strength == Strength.LIGHT

println customCoffee


