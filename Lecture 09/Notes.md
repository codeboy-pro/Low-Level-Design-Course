# Lecture 09 — Factory Design Patterns

## Overview

This lecture covers the **Creational Design Patterns** from the Factory family. These patterns deal with **object creation** — abstracting away the `new` keyword so the client code doesn't need to know which concrete class it is working with.

Three patterns are covered, each progressively more powerful:

| Pattern          | Factory is                                  | Creates                                                          |
| ---------------- | ------------------------------------------- | ---------------------------------------------------------------- |
| Simple Factory   | A plain class with a static/instance method | One product type                                                 |
| Factory Method   | An abstract class / interface               | One product type, but each subclass decides the concrete product |
| Abstract Factory | An interface with multiple factory methods  | A **family** of related products                                 |

---

## 1. Simple Factory

### What is it?

A **Simple Factory** is not a formal GoF design pattern — it is a programming idiom. The idea is to centralise object creation in one place (a factory class) rather than scattering `new ConcreteClass()` calls across the codebase.

### Why do we need it?

Without a factory, the client must know every concrete class:

```cpp
// Client is tightly coupled to every burger type
if (type == "basic")    burger = new BasicBurger();
if (type == "standard") burger = new StandardBurger();
if (type == "premium")  burger = new PremiumBurger();
```

If a new burger type is added, **every** call site must be updated.

### Structure

```
Client  ──uses──▶  BurgerFactory.createBurger(type)
                        │
              ┌─────────┼─────────┐
              ▼         ▼         ▼
         BasicBurger  StandardBurger  PremiumBurger
              └─────────┴─────────┘
                    (all implement Burger)
```

### Code Walkthrough (C++)

```cpp
// Abstract product
class Burger {
public:
    virtual void prepare() = 0;
    virtual ~Burger() {}
};

// Concrete products
class BasicBurger : public Burger {
public:
    void prepare() override {
        cout << "Preparing Basic Burger with bun, patty, and ketchup!" << endl;
    }
};

class StandardBurger : public Burger { /* ... */ };
class PremiumBurger  : public Burger { /* ... */ };

// The Simple Factory
class BurgerFactory {
public:
    Burger* createBurger(string& type) {
        if (type == "basic")    return new BasicBurger();
        if (type == "standard") return new StandardBurger();
        if (type == "premium")  return new PremiumBurger();
        return nullptr;
    }
};

// Client
int main() {
    string type = "standard";
    BurgerFactory* factory = new BurgerFactory();
    Burger* burger = factory->createBurger(type);
    burger->prepare();   // "Preparing Standard Burger..."
}
```

### Key Points

- The client only depends on the **abstract `Burger`** type and `BurgerFactory` — never on concrete classes.
- Adding a new burger type requires changing only `BurgerFactory::createBurger` (one place).
- **Limitation:** The factory class itself is not extensible — to support a completely different set of burgers (e.g., a different brand), you have to modify the factory class directly, violating **OCP (Open/Closed Principle)**.

---

## 2. Factory Method

### What is it?

The **Factory Method** pattern defines an **interface (or abstract class) for creating an object**, but lets **subclasses decide which class to instantiate**. The factory method is deferred to the subclass.

This solves the OCP problem of the Simple Factory: instead of changing one class, you **extend** by adding new factory subclasses.

### Motivation (from the example)

Imagine a new burger chain, **KingBurger**, that uses wheat buns for all their burgers:

- `BasicWheatBurger`, `StandardWheatBurger`, `PremiumWheatBurger`

With Simple Factory you'd have to edit `BurgerFactory` and add an `if (brand == "king")` block. With Factory Method you just create a new `KingBurger` factory class.

### Structure

```
       «abstract»
      BurgerFactory
   createBurger(type)*          ← factory method (abstract)
         /         \
  SinghBurger    KingBurger
  creates         creates
  Basic/Standard  BasicWheat/StandardWheat
  /PremiumBurger  /PremiumWheatBurger
```

### Code Walkthrough (C++)

```cpp
// Abstract factory (declares the factory method)
class BurgerFactory {
public:
    virtual Burger* createBurger(string& type) = 0;
};

// Concrete factory 1 — regular bun burgers
class SinghBurger : public BurgerFactory {
public:
    Burger* createBurger(string& type) override {
        if (type == "basic")    return new BasicBurger();
        if (type == "standard") return new StandardBurger();
        if (type == "premium")  return new PremiumBurger();
        return nullptr;
    }
};

// Concrete factory 2 — wheat bun burgers
class KingBurger : public BurgerFactory {
public:
    Burger* createBurger(string& type) override {
        if (type == "basic")    return new BasicWheatBurger();
        if (type == "standard") return new StandardWheatBurger();
        if (type == "premium")  return new PremiumWheatBurger();
        return nullptr;
    }
};

// Client
int main() {
    string type = "basic";
    BurgerFactory* myFactory = new SinghBurger();   // swap to KingBurger anytime
    Burger* burger = myFactory->createBurger(type);
    burger->prepare();   // "Preparing Basic Burger..."
}
```

### Key Points

- **Open/Closed Principle:** Adding a new brand means adding a new class, not modifying existing ones.
- The client talks only to `BurgerFactory*` — it is **brand-agnostic**.
- Still creates **one type of product** (only `Burger`). When you need to create **multiple related products**, use Abstract Factory.

### Simple Factory vs Factory Method
                                                                    
|            | Simple Factory          | Factory Method             |
| ---------- | ----------------------- | -------------------------- |
| Factory    | Concrete class          | Abstract class / interface |
| Extension  | Modify existing factory | Add new factory subclass   |
| OCP        | Violates OCP            | Follows OCP                |
| Complexity | Low                     | Medium                     |

---

## 3. Abstract Factory

### What is it?

The **Abstract Factory** pattern provides an interface for creating **families of related or dependent objects** without specifying their concrete classes.

Think of it as a _factory of factories_. Where Factory Method has one factory method per product, Abstract Factory groups **multiple factory methods** into a single interface — one per product in the family.

### Motivation (from the example)

Each burger outlet now needs to produce a **complete meal** — a `Burger` AND a `GarlicBread`. The two products must be consistent:

- `SinghBurger` outlet → regular bun `Burger` + regular `GarlicBread`
- `KingBurger` outlet → wheat bun `Burger` + wheat `GarlicBread`

If you used two separate Factory Method factories, nothing would enforce that consistent pairing. Abstract Factory groups them.

### Structure

```
         «interface»
         MealFactory
     ┌───────────────────┐
     │ createBurger()    │   ← factory method for Product 1
     │ createGarlicBread()│  ← factory method for Product 2
     └───────────────────┘
            /         \
     SinghBurger     KingBurger
     (regular bun)   (wheat bun)
     creates:        creates:
     BasicBurger     BasicWheatBurger
     CheeseGarlicBread  CheeseWheatGarlicBread
     etc.            etc.
```

### Code Walkthrough (C++)

```cpp
// Two product hierarchies
class Burger     { public: virtual void prepare() = 0; };
class GarlicBread{ public: virtual void prepare() = 0; };

// Abstract Factory — groups both factory methods
class MealFactory {
public:
    virtual Burger*      createBurger(string& type)      = 0;
    virtual GarlicBread* createGarlicBread(string& type) = 0;
};

// Concrete factory 1 — regular outlet
class SinghBurger : public MealFactory {
public:
    Burger* createBurger(string& type) override {
        if (type == "basic")    return new BasicBurger();
        if (type == "standard") return new StandardBurger();
        if (type == "premium")  return new PremiumBurger();
        return nullptr;
    }
    GarlicBread* createGarlicBread(string& type) override {
        if (type == "basic")  return new BasicGarlicBread();
        if (type == "cheese") return new CheeseGarlicBread();
        return nullptr;
    }
};

// Concrete factory 2 — wheat outlet
class KingBurger : public MealFactory {
public:
    Burger* createBurger(string& type) override {
        if (type == "basic")    return new BasicWheatBurger();
        if (type == "standard") return new StandardWheatBurger();
        if (type == "premium")  return new PremiumWheatBurger();
        return nullptr;
    }
    GarlicBread* createGarlicBread(string& type) override {
        if (type == "basic")  return new BasicWheatGarlicBread();
        if (type == "cheese") return new CheeseWheatGarlicBread();
        return nullptr;
    }
};

// Client
int main() {
    string burgerType     = "basic";
    string garlicBreadType = "cheese";

    MealFactory* mealFactory = new KingBurger();

    Burger*      burger     = mealFactory->createBurger(burgerType);
    GarlicBread* garlicBread = mealFactory->createGarlicBread(garlicBreadType);

    burger->prepare();      // "Preparing Basic Wheat Burger..."
    garlicBread->prepare(); // "Preparing Cheese Wheat Garlic Bread..."
}
```

### Key Points

- The client interacts only with `MealFactory*` and the abstract product types — completely decoupled from concrete classes.
- Swapping `new KingBurger()` for `new SinghBurger()` changes the **entire product family** atomically.
- Products within one factory are always **compatible with each other** (wheat burger + wheat garlic bread).
- **Limitation:** Adding a new product to the family (e.g., `Drink`) requires changing the abstract `MealFactory` interface and all concrete factories — this can be costly.

---

## Comparison: All Three Patterns

| Aspect          | Simple Factory              | Factory Method                    | Abstract Factory                               |
| --------------- | --------------------------- | --------------------------------- | ---------------------------------------------- |
| What it creates | One product                 | One product                       | A family of products                           |
| Factory type    | Concrete class              | Abstract class                    | Abstract interface                             |
| How to extend   | Modify factory              | Add new factory subclass          | Add new factory subclass + new product classes |
| SOLID — OCP     | Violates                    | Follows                           | Follows                                        |
| SOLID — SRP     | ✅                          | ✅                                | ✅                                             |
| Complexity      | Low                         | Medium                            | High                                           |
| Use when        | Simple centralised creation | Different variants of one product | Multiple related products must stay consistent |

---

## When to Use Which

- **Simple Factory** — Good starting point when creation logic is simple and unlikely to change. Quick to implement.
- **Factory Method** — When you anticipate different "brands" or "implementations" of the same product, and you want to follow OCP by adding new subclasses instead of editing existing code.
- **Abstract Factory** — When you have **multiple products that belong together** and the client must always use a consistent family (e.g., GUI toolkit widgets: all Windows-style or all Mac-style).

---

## Real-World Analogies

| Pattern          | Analogy                                                                                                            |
| ---------------- | ------------------------------------------------------------------------------------------------------------------ |
| Simple Factory   | A single chef who makes any dish you order                                                                         |
| Factory Method   | Different restaurant franchises (SinghBurger, KingBurger), each with their own recipe for the same menu items      |
| Abstract Factory | A franchise kit — a full bundle of recipes and ingredients that guarantees everything is consistent with the brand |

---

## Design Principles Applied

- **OCP (Open/Closed Principle):** Factory Method and Abstract Factory let you add new product types without modifying existing factory code.
- **DIP (Dependency Inversion Principle):** Clients depend on abstract factory interfaces and abstract product types, not concrete implementations.
- **SRP (Single Responsibility Principle):** Creation logic is separated from business logic — the factory is solely responsible for instantiation.
