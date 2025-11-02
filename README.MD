Voici un README.md complet pour votre projet :

```markdown
# Bit Packing Compression - Software Engineering Project 2025

**Auteur** : RemiM06

Projet de compression d'entiers utilisant la technique du Bit Packing pour accélérer les transmissions réseau.

---

## 📋 Table des matières

1. [Description du projet](#description-du-projet)
2. [Prérequis](#prérequis)
3. [Installation](#installation)
4. [Utilisation](#utilisation)
5. [Stratégies de compression](#stratégies-de-compression)
6. [Architecture du code](#architecture-du-code)
7. [Résultats des benchmarks](#résultats-des-benchmarks)

---

## 🎯 Description du projet

Ce projet implémente plusieurs stratégies de compression d'entiers basées sur le **Bit Packing** :

- **Overlap** : Les entiers compressés peuvent chevaucher deux int32 consécutifs
- **No-Overlap** : Chaque entier compressé reste dans un seul int32
- **Overflow** : Utilise une zone de débordement pour les valeurs exceptionnelles

L'objectif est de réduire la taille des tableaux d'entiers transmis sur le réseau, tout en conservant un **accès direct** aux éléments (fonction `get(i)` sans décompression complète).

---

## 🔧 Prérequis

- **Java** : JDK 20 ou supérieur
- **Maven** : 3.6+ (pour la compilation)

---

## 📦 Installation

### 1. Cloner le dépôt

```bash
git clone <votre-url-github>
cd SoftwareEngineering-DataCompressing
```

### 2. Compiler le projet

```bash
mvn clean compile
```

---

## 🚀 Utilisation

### Exécution du programme principal

```bash
mvn exec:java -Dexec.mainClass="org.example.Main"
```

### Sortie attendue

Le programme affiche :
- Comparaison des performances entre les stratégies
- Temps de compression, décompression et accès `get()`
- Analyse de rentabilité pour la transmission réseau

---

## 📖 Utilisation dans votre code

### Exemple 1 : Compression simple avec Overlap

```java
import org.example.*;

public class Example {
    public static void main(String[] args) {
        int[] data = {1, 2, 3, 1024, 4, 5};
        
        // Créer un compresseur avec chevauchement
        BitPacking bp = BitPackingFactory.create(CompressionType.OVERLAP);
        
        // Compresser
        int[] compressed = bp.compress(data);
        System.out.println("Taille compressée : " + bp.getCompressedSize());
        System.out.println("Ratio : " + bp.getCompressionRatio());
        
        // Accès direct au 3e élément (sans décompression)
        int value = bp.get(2);
        System.out.println("data[2] = " + value);
        
        // Décompression complète
        int[] decompressed = bp.decompress();
    }
}
```

### Exemple 2 : Compression sans chevauchement

```java
BitPacking bp = BitPackingFactory.create(CompressionType.NO_OVERLAP);
int[] compressed = bp.compress(data);
```

### Exemple 3 : Compression avec overflow

```java
BitPacking bp = BitPackingFactory.create(CompressionType.OVERFLOW);
int[] compressed = bp.compress(data);
```

---

## 🔍 Stratégies de compression

### 1. **Overlap** (avec chevauchement)

**Principe** : Utilise chaque bit disponible, même si cela signifie que certains entiers s'étendent sur deux int32.

**Avantages** :
- Meilleur ratio de compression (15% supérieur)
- Pas de bits perdus (padding minimal)

**Inconvénients** :
- Compression plus lente (3x)
- Code plus complexe

**Exemple** : Pour 6 entiers sur 12 bits chacun :
```
int32[0] : élément 0 (bits 0-11), élément 1 (bits 12-23), élément 2 (bits 24-31 + overflow)
int32[1] : élément 2 (bits 0-3), élément 3 (bits 4-15), élément 4 (bits 16-27), élément 5 (bits 28-31 + overflow)
int32[2] : élément 5 (bits 0-7)
```

---

### 2. **No-Overlap** (sans chevauchement)

**Principe** : Chaque entier compressé reste dans un seul int32, quitte à laisser des bits inutilisés.

**Avantages** :
- Compression 3x plus rapide
- Code plus simple
- Accès plus direct

**Inconvénients** :
- Moins bon ratio (padding obligatoire)

**Exemple** : Pour 6 entiers sur 12 bits chacun :
```
int32[0] : élément 0 (bits 0-11), élément 1 (bits 12-23), [8 bits inutilisés]
int32[1] : élément 2 (bits 0-11), élément 3 (bits 12-23), [8 bits inutilisés]
int32[2] : élément 4 (bits 0-11), élément 5 (bits 12-23), [8 bits inutilisés]
```

---

### 3. **Overflow** (avec zone de débordement)

**Principe** : Compresse les valeurs "normales" avec peu de bits, et stocke les outliers dans une zone séparée.

**Exemple** : Pour `[1, 2, 3, 1024, 4, 5, 2048]` :
- Seuil : 90e percentile (ici, 5)
- Zone principale : `[0-1, 0-2, 0-3, 1-0, 0-4, 0-5, 1-1]` (3 bits + 1 bit flag)
- Zone overflow : `[1024, 2048]`

---

## 🏗️ Architecture du code

```
src/main/java/org/example/
├── BitPacking.java               # Interface commune
├── BitPackingOverlap.java        # Implémentation avec chevauchement
├── BitPackingNoOverlap.java      # Implémentation sans chevauchement
├── BitPackingOverflow.java       # Implémentation avec overflow
├── BitPackingFactory.java        # Factory pour créer les instances
├── CompressionType.java          # Enum des types
├── BitPackingUtils.java          # Utilitaires (calcul de bits)
├── BitPackingBenchmark.java      # Tests de performance
├── TransmissionAnalysis.java     # Calcul de rentabilité
└── Main.java                     # Point d'entrée
```

---

## 📊 Résultats des benchmarks

**Dataset** : 10 000 entiers (95% entre 0-100, 5% outliers ~10 000)

| Métrique           | Overlap  | NoOverlap | Gagnant   |
|--------------------|----------|-----------|-----------|
| Temps compression  | 2406 µs  | 835 µs    | NoOverlap |
| Temps décompression| 918 µs   | 654 µs    | NoOverlap |
| Temps get() moyen  | 110 ns   | 114 ns    | Similaire |
| Ratio compression  | 2.29     | 2.00      | Overlap   |
| Seuil rentabilité  | 0.097 µs | 0.095 µs  | Similaire |

### Interprétation

**Quand utiliser NoOverlap ?**
- Compressions fréquentes (3x plus rapide)
- Applications temps réel
- Ressources CPU limitées

**Quand utiliser Overlap ?**
- Bande passante limitée (15% d'économie)
- Transmissions longue distance
- Stockage long terme

---

## 📄 Licence

Projet académique - Université Nice Sophia Antipolis - 2025

---

## 📧 Contact

**Auteur** : Remi Mathieu
**Email du professeur** : jcregin@gmail.com
```
