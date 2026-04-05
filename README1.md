## -------------------------------------------------------------------
# jhotdraw projet
# Génie logiciel JHotDraw
## Authors:Gamal Daoud Youssouf
## Groupe: 1
## Partie 2 : Amélioration du projet
## ---------------------------------------------------------------------

## Enseignante : Madame   Imen Sayar  (imen.sayar@univ-lille.fr)
Lien:
- Commit : lien vers le commit
`Lien du projet :  https://github.com/gamal-daoud/jhotdraw

## Objectifs de ce projet :
Étudier la qualité logicielle, Amélioration du projet.

## Introduction
Suite à l'évаluаtiоn de lа qualité lоgiсiellе de JHоtDraw (jhоtdraw-соre) dаns la première sеctiоn, ce rappоrt met en lumièrе lеs аméliоratiоns misеs en œuvrе dans le prоjet. Les ajustements, оrganisés par niveаu dе соmpleхité, оnt pоur оbjectif dе rеnfоrсer la cоhérеnce et la mаintеnabilité du cоde еn se cоncеntrant sur plusiеurs classеs еssentielles. Chaquе mоdificatiоn est sоignеusemеnt décritе, incluant l'étаt initial, lеs сhangemеnts еffеctués ainsi que les améliоratiоns appоrtéеs au fichiеr README.

## 6 Petites modifications:
Ces modifications visaient à nettoyer le code, corriger des ambiguïtés et des bugs mineurs évidents pour améliorer la lisibilité globale.

- les modifications apportées à la classe SlantedLiner et les raisons de ces changements :

# 1 Ajout de la constante DEFAULT_SLANT_SIZE
Ce qui a été modifié : remplacer le nombre magique 20 dans le constructeur par défaut par la constante DEFAULT_SLANT_SIZE.
Raison : Améliorer la lisibilité et la maintenabilité du code en donnant un nom explicite à cette valeur par défaut. Cela permet de comprendre immédiatement sa signification et facilite une éventuelle modification future.


# 2 Ajout des constantes PATH_SIZE_TWO_FIGURES et PATH_SIZE_SAME_FIGURE
Ce qui a été modifié : Remplacer les nombres magiques 4 et 5 par ces constantes dans les boucles while qui ajustent le nombre de nœuds du BezierPath.
Raison : Clarifier et cela rend le code auto-documenté et réduit les risques d'erreur si ces valeurs doivent être modifiées.

# 3 Ajout de constantes pour les indices de nœuds
Ce qui a été modifié : Remplacer les indices littéraux 0, 1, 2, 3 et la position d'insertion 1 par les constantes :

- START_NODE (0)
- FIRST_INTERMEDIATE_NODE (1)
- SECOND_INTERMEDIATE_NODE (2)
- THIRD_INTERMEDIATE_NODE (3)
- INSERT_INDEX (1)

Raison :rendre le code plus explicite egalement cela améliore la compréhension du code et évite d'utiliser des nombres "magiques" qui n'ont pas de signification évidente.

Ce qui a été modifié aussi : Dans le else (deux figures différentes), les boucles while utilisaient encore les nombres 4 et - Elles ont été remplacées par PATH_SIZE_TWO_FIGURES et INSERT_INDEX.
Raison : Assurer la cohérence avec le reste du code et éliminer tous les nombres magiques résiduels. Cela garantit que toute modification future de ces valeurs se fera via les constantes.

https://github.com/gamal-daoud/jhotdraw/blob/develop/jhotdraw-core/src/main/java/org/jhotdraw/draw/liner/SlantedLiner.java

Lien
- Commit : lien vers le commit
https://github.com/gamal-daoud/jhotdraw/commit/567c31a291c33b4462062d90c523b96a1b94ebe2


# Extraction des nombres magiques
# Ajout de trois constantes en tête de classe :
Les nombres magiques pour la tolérance de clic et le multiplicateur de croissance ont été extraits dans des constantes protégées
Justification: Améliore la lisibilité et facilite la maintenance.

- HIT_TOLERANCE = 1.0 (remplace 1f utilisé dans contains)

- HIT_GROWTH_MULTIPLIER = 2.0 (remplace le facteur 2 dans contains)

- SPLIT_TOLERANCE = 5f


#  Renommage de variables :
- Tous les paramètres de type Point2D.Double initialement nommés p (find, join, split) ont été renommés en point (dans findConnector, contains, findNode, chop, handleMouseClick).
- Tous les paramètres de type Graphics2D nommés g ont été renommés en graphics (dans drawStroke, drawCaps, drawFill)

https://github.com/gamal-daoud/jhotdraw/blob/develop/jhotdraw-core/src/main/java/org/jhotdraw/draw/figure/BezierFigure.java

Lien
- Commit : lien vers le commit
https://github.com/gamal-daoud/jhotdraw/commit/98ba34de01e85e2c21ac3145c63b8dd7efc42e38



## 7 Moyennes modifications

# Ajouter des commentaires
Des commentaire sont ajouté toutes les méthodes de la classe Attributes
de paquetage figure pour comprendre ce qui fais la méthode.
Par exemple:

/*
   * Removes an attribute from the figure and calls {@code attributeChanged} on all registered {@code FigureListener}s if the attribute value has changed.
   * For efficiency reasons, the drawing is not automatically repainted. If you want the drawing to be repainted when the attribute is changed, you can either use {@code key.remove(figure); } or
   * <pre>
   * figure.willChange();
   * figure.remove(...);
   * figure.changed();
   * </pre>
   * @see AttributeKey#remove
   **
  * @param key The attribute key to remove.
  * @param <T> The type of the attribute value.
  * @return Returns the old value of the attribute that was removed, or {@code null} if the
    attribute
  *
  was not set before.
  * @see AttributeKey#remove
  */

  public <T> void removeAttribute(AttributeKey<T> key) {
  ..........
  }


  /**
   * Sets an attribute on the figure and calls {@code attributeChanged} on all registered {@code
   * FigureListener}s if the attribute value has changed.
   *
   * <p>For efficiency reasons, the drawing is not automatically repainted. If you want the drawing
   * to be repainted when the attribute is changed, you can either use {@code key.set(figure,
   * value); } or
   *
   * <pre>
   * figure.willChange();
   * figure.set(...);
   * figure.changed();
   * </pre>
   *
   * @see AttributeKey#set
   */
  public <T> Attributes set(final AttributeKey<T> key, final T newValue) {
   ......
    }

jhotdraw-core/src/main/java/org/jhotdraw/draw/figure/Attributes.java
Lien
- Commit : lien vers le commit
https://github.com/wumpz/jhotdraw/commit/e14f513a6a4533465430242c2dd68c1df0363a5e

 Réorganisation et Renommage dans la classe Attributes
Fichier : `org.jhotdraw.draw.figure.Attributes.java`

## Ce qui a été fait :
1. Réorganisation de la structure de la classe:
   - Les variables d'instance ont été regroupées en début de classe.
   - Les constructeurs ont été placés après les variables.
   - Les méthodes publiques (d'instance puis statiques) suivent.
   - Les méthodes privées (comme `fireAttributeChanged`) ont été déplacées à la fin.
   - L'interface interne `AttributeListener` a également été déplacée à la fin.

-Raison: Suivre les conventions standard de Java pour améliorer la lisibilité et la navigation dans le code. Les méthodes privées et les interfaces internes ne doivent pas interrompre le flux des méthodes publiques.

2.Correction des violations de nommage:
   - L'attribut `attributes` a été renommé en `attributesCollection`.
   - L'attribut `DEPENDENT` a été renommé en `dependent`.
- Raison: Éviter la confusion entre le nom de l'attribut et le nom de la classe (`Attributes`), et respecter la convention de nommage `camelCase` pour les variables non constantes.

3.Correction d'un bug de masquage de variable (Variable Shadowing):
   - Dans la méthode `getAttributesRestoreData()`, la variable locale `dependent` masquait le champ `this.dependent`, ce qui pouvait causer des erreurs de référence. Elle a été renommée en `dependentList`.
- Raison: Assurer que le code est robuste et éviter les erreurs logiques dues au masquage de variables de classe par des variables locales.

Lien
- Commit : lien vers le commit
https://github.com/gamal-daoud/jhotdraw/commit/320a1f78452e117e4e087b73bb935b58a748e041


# Suppression de duplication de code — `DefaultDrawingView` :
Lien
- Commit : lien vers le commit
https://github.com/gamal-daoud/jhotdraw/commit/f41034ea044a6fbbead3b5379e57c135ab666ed1

Le problème central était dans les méthodes (drawDrawingVolatileBuffered et drawDrawingNonvolatileBuffered)
contenaient deux blocs identiques:
- Bloc 1 : calcul du shift et mise à jour de bufferedArea/dirtyArea

- Bloc 2 : effacement et repaint de la zone sale
Ces blocs ont été extraits dans deux méthodes privées :
`updateBufferedAreaAndShift` et `repaintDirtyBufferArea`.


##  Analyse avant / après — SonarQube:

#### Duplications
-------------------------------------------------
| Métrique         | Avant | Après | Gain       |
|------------------|-------|-------|------------|
| Density          | 46.0% | 32.7% | -13.4%     |
| Duplicated Lines | 714   | 494   |-220 lignes |
| Duplicated Blocks| 19    |  15   | -4 blocs   |
-------------------------------------------------
table 1

#### Complexité
-----------------------------------------------
| Métrique             | Avant | Après | Gain |
|----------------------|-------|-------|------|
| Cyclomatic Complexity| 265   | 256   | -9   |
| Cognitive Complexity | 279   | 255   | -24  |
-----------------------------------------------
table 2

#### Taille et qualité
------------------------------------------------
| Métrique    | Avant     | Après | Gain       |
|-------------|-----------|-------|------------|
| Lines codess| ~1 600    | 1 191 |-409 lignes |
| Code Smells | 63        | 0     | -63        |
| Functions   | —         | 94    | +2         |
------------------------------------------------

table 3

#### Principe appliqué et ce qui a été fait.

- Supprimer la duplication : blocs identiques extraits en méthodes updateBufferedAreaAndShift et repaintDirtyBufferArea.
- Décomposer une méthode mixte : BufferUpdateResult sépare proprement retour et effet de bord.
- Réduire la complexité cyclomatique : chaque méthode résultante a moins de branches imbriquées.

Autrement dit le gain en Cognitive Complexity (-24  sur table 2) est plus important que le gain en Cyclomatic (-9 ). C'est logique : la Cognitive Complexity pénalise les structures imbriquées. En extrayant les if/else if chaînés du shift hors des méthodes principales, on réduit la profondeur d'imbrication perçue par le lecteur, ce que SonarQube récompense davantage.

La réduction de (~409 sur table 3) lignes vient du fait que les deux blocs dupliqués ont été remplacés par un simple appel de méthode chacun. On ajoute 2 fonctions mais on gagne -63 Code Smells — SonarQube considère que le code est maintenant plus lisible et maintenable.

# Ce que les chiffres confirment en expliquant
La duplication était le problème principal.220 lignes dupliquées sur 1 600 = c'est exactement les deux blocs identiques dans drawDrawingVolatileBuffered et drawDrawingNonvolatileBuffered. L'extraction en méthodes privées les a éliminés proprement.

La complexité cognitive chute plus que la cyclomatique parce que les if/else if du calcul de shift étaient profondément imbriqués dans une boucle while(true) elle-même dans la méthode principale. En les sortant dans updateBufferedAreaAndShift, le niveau d'imbrication maximal des deux méthodes appelantes diminue.

Les Code Smells passent de 63 à 0 (-63) confirme que SonarQube reconnaît directement la suppression de duplication comme une amélioration de maintenabilité, pas seulement un changement cosmétique.


# Déclaration d'une classe interne BufferUpdateResult
BufferUpdateResult comme classe interne statique privée dans (DefaultDrawingView) parce qu'elle sert uniquement à retourner deux valeurs depuis updateBufferedAreaAndShift (shift et needsRecreate), ce que Java ne permet pas nativement. Elle est private static final donc invisible de l'extérieur, sans référence à l'instance parente, et sans risque de fuite mémoire. C'est la solution propre pour décomposer une méthode qui à la fois modifie l'état et retourne des informations.

# Ajout d'une classe de test
DefaultDrawingViewTest pour vérifier que les méthodes publiques de DefaultDrawingView se comportent correctement de façon isolée. Chaque test vérifie un seul comportement précis (scale, sélection, constrainer, etc.) sans dépendre des autres. C'est une moyenne modification selon les principes de refactoring : ajouter un test pertinent qui couvre le code modifié et garantit que le comportement reste stable après refactoring.

Lien
- Commit : lien vers le commit
https://github.com/gamal-daoud/jhotdraw/commit/20b469e1bf432e0443e3c36e954b9bff6239bf7b



### Grandes modifications (suite des modifications sur la God Class DefaultDrawingView)
Une partie des modifications à été faites dans les moyennes modifications


## Extraction de la gestion des handles — HandleManager:

# Problème initial:
DefaultDrawingView (environ 2600 lignes) concentrait de multiples responsabilités : rendu, gestion des buffers, sélection des figures, et gestion des handles (handles de sélection et secondaires). Cette accumulation violait le principe de responsabilité unique (SRP) et rendait la classe difficile à maintenir et à tester.

Solution
Une nouvelle classe HandleManager a été créée pour isoler toute la logique relative aux handles. Elle prend en charge :
- Les listes de handles de sélection (selectionHandles) et secondaires (secondaryHandles).
- L'état de validité des handles (handlesAreValid).
- Le handle actif (activeHandle).
- Les opérations de validation, invalidation, ajout, suppression, recherche et compatibilité.

Liens
- Commit : lien vers le commit
https://github.com/gamal-daoud/jhotdraw/commit/291abe01e5966f2a275812af56f1735d5e727323


# Modifications apportées
Dans DefaultDrawingView :
- Suppression des attributs selectionHandles, secondaryHandles, handlesAreValid, activeHandle.
- Ajout d'une instance handleManager et délégation de toutes les méthodes concernées (ex. getSelectionHandles(), invalidateHandles(), findHandle(), setActiveHandle()).
- Adaptation des événements (handleRequestSecondaryHandles, handleRequestRemove) pour utiliser le manager.

# Code concerné
- Nouvelle classe : org.jhotdraw.draw.handle.HandleManager (environ 120 lignes).
- Modifications dans DefaultDrawingView : suppression de ~150 lignes de code liées aux handles.


# Bénéfices et pourqui ces modifications?

- Respect du principe de responsabilité unique : DefaultDrawingView se concentre désormais sur son rôle de vue.
- Meilleure maintenabilité : les bugs relatifs aux handles sont isolés dans une classe dédiée.
- Réduction de la taille de DefaultDrawingView (~200 lignes en moins).
- Préparation à l'introduction d'un pattern MVC ou d'un renderer spécifique pour le rendu.

Liens
- Commit : lien vers le commit
https://github.com/gamal-daoud/jhotdraw/commit/0291848e529853769daa27ac8564aa2eb7137eab



## 1 Petites modifications:

### Extraction
des nombres magiques dans BezierFigure.java
## Fichier modifié :
 `org.jhotdraw.draw.figure.BezierFigure`

## Ce qui a été modifié :
Extraction des tolérances de test de collision (`HIT_TOLERANCE`, `HIT_GROWTH_MULTIPLIER`) et de division de segment (`SPLIT_TOLERANCE`) en constantes statiques finales.
- Raison :
Élimine les "nombres magiques" pour améliorer la lisibilité et faciliter la maintenance.


### Extraction
des nombres magiques dans LineConnectionFigure.java
## Fichier modifié :
`org.jhotdraw.draw.figure.LineConnectionFigure`
## Ce qui a été modifié :
Remplacement de la valeur `5f` par la constante `SPLIT_TOLERANCE`.
- Raison :
Cohérence avec `BezierFigure` et documentation explicite de la valeur de tolérance.

### Renommage de variables
dans LineConnectionFigure.reverseConnection()
## Fichier modifié :
`org.jhotdraw.draw.figure.LineConnectionFigure`
## Ce qui a été modifié :
Renommage de `tmpC` en `oldStartConnector` et `tmpP` en `oldStartPoint`.
- Raison :
Améliore la clarté du code en utilisant des noms descriptifs qui reflètent le rôle des variables.

### Suppression du code mort
dans BezierFigure et LineConnectionFigure
## Fichiers modifiés :
`BezierFigure.java`, `LineConnectionFigure.java`
## Ce qui a été modifié :
Suppression de blocs de code commentés et de commentaires `FIXME` obsolètes.
- Raison :
Nettoyage de la base de code pour réduire le bruit visuel et améliorer la maintenabilité.

### Réorganisation de la structure de BezierFigure.java
## Fichier modifié :
`BezierFigure.java`
## e qui a été modifié :
Réorganisation des membres de la classe pour suivre l'ordre standard : constantes, champs, constructeurs, méthodes publiques, puis méthodes protégées/privées.
- Raison :
Facilite la navigation dans le code et respecte les conventions de structure de classe Java.

### Amélioration
du nommage des paramètres dans BezierFigure.setPoint
## Fichier modifié :
`BezierFigure.java`
## Ce qui a été modifié :
Renommage des paramètres `p` en `newPoint` et `ctrlPntIndex` en `controlPointIndex`.
- Raison :
Plus grande expressivité des signatures de méthodes.


## 2 Moyennes et Grande modifications:
### Réduction de la complexité cyclomatique
Réduction de la complexité cyclomatique  de getCappedPath()
##  Fichier modifié :
`BezierFigure.java`
## Ce qui a été modifié :
Décomposition de la méthode `getCappedPath()` en extrayant la logique de capping du début et de la fin du chemin dans les méthodes `capStart()` et `capEnd()`.
- Raison :
Diminue la complexité d'une méthode trop longue et améliore la modularité du code.

Liens
- Commit : lien vers le commit
https://github.com/gamal-daoud/jhotdraw/commit/ef5c9f64b2c3ee561a037ac84510aa6ba9f0ea83

https://github.com/gamal-daoud/jhotdraw/commit/bd9fb36f84acdbdbdcc17fce6e8cb97ed3305812



## 8 Grandes modifications


## Ajout d'une super-classe `AbstractLinearLayouter` pour supprimer des méthodes dupliquées

### Problème initial

Le package `org.jhotdraw.draw.layouter` contenait deux classes `HorizontalLayouter` et `VerticalLayouter`, héritant toutes deux de `AbstractLayouter`. En les comparant, on constate que leurs méthodes `calculateLayout()` et `layout()` sont **structurellement identiques** : même algorithme, mêmes structures de contrôle (`for`, `switch` sur `Alignment`), mêmes formules mathématiques. Seule la direction (axe X pour l'horizontal, axe Y pour le vertical) change. Cette duplication représentait environ **~180 lignes dupliquées** entre les deux fichiers, ce qui constitue une violation directe du principe DRY (*Don't Repeat Yourself*).

```
Avant :
  AbstractLayouter
       ├── HorizontalLayouter   (calculateLayout + layout)
       └── VerticalLayouter     (calculateLayout + layout quasi-identiques)
```

### Solution : nouvelle super-classe abstraite `AbstractLinearLayouter`

Une nouvelle classe abstraite `AbstractLinearLayouter` (entre `AbstractLayouter` et les deux layouters) a été introduite. Elle implémente les méthodes `calculateLayout()` et `layout()` de façon générique, en s'appuyant sur un ensemble de méthodes abstraites qui définissent **l'axe de disposition** :
--------------------------------------------------------------------------------------
| Méthode abstraite            | `HorizontalLayouter`    | `VerticalLayouter`        |
|----------------------------- |-------------------------|---------------------------|
| `getPrimaryPreferredSize()`  | `size.width`            | `size.height`             |
| `getSecondaryPreferredSize()`| `size.height`           | `size.width`              |
| `getPrimaryInsets()`         | `left + right`          | `top + bottom`            |
| `getSecondaryInsets()`       | `top + bottom`          | `left + right`            |
| `getPrimaryBound()`          | `rect.width`            | `rect.height`             |
| `getSecondaryBound()`        | `rect.height`           | `rect.width`              |
| `buildChildBounds()`         | alignement **vertical** | alignement **horizontal** |
--------------------------------------------------------------------------------------

```
Après :
  AbstractLayouter
       └── AbstractLinearLayouter   (calculateLayout + layout partagés)
                ├── HorizontalLayouter  (surcharge des hooks de direction)
                └── VerticalLayouter    (surcharge des hooks de direction)
```

### Fichiers modifiés / créés
--------------------------------------------------------------------------------------------------------------------|
| Fichier                                 | Action                                                                  |
|-------------------------------------------------------------------------------------------------------------------|
| `layouter/AbstractLinearLayouter.java`  =>  **[NOUVEAU]** Superclasse commune avec l'algorithme générique         |
| `layouter/HorizontalLayouter.java`      => **[MODIFIÉ]** Étend désormais `AbstractLinearLayouter`; toute la logique dupliquée supprimée (lignes retirées)                                                                           |
| `layouter/VerticalLayouter.java`        =>  **[MODIFIÉ]** Étend désormais `AbstractLinearLayouter`; toute la logique dupliquée supprimée (lignes retirées)                                                                           |
|-------------------------------------------------------------------------------------------------------------------|

### Bénéfices

- **Suppression de des lignes dupliquées** entre les deux classes.
- **Maintenabilité** : tout bug ou évolution dans l'algorithme de layout ne s'applique qu'une seule fois dans `AbstractLinearLayouter`.
- **Extensibilité** : pour ajouter un `DiagonalLayouter` ou un `FlowLayouter`, il suffit de sous-classer `AbstractLinearLayouter` et d'implémenter les hooks de direction.
- **Principe ouvert/fermé** : les sous-classes sont fermées à la modification de l'algorithme général, mais ouvertes à l'extension de la direction.

### Principe appliqué
**Template Method Pattern** : `AbstractLinearLayouter` définit le squelette de l'algorithme (`calculateLayout` / `layout`) et délègue les variations de direction à des méthodes abstraites (*hooks*) que les sous-classes concrètes implémentent.

Lien
- Commit : lien vers le commit
https://github.com/gamal-daoud/jhotdraw/commit/83a518697067f4a6be62dfe4b496f8d16f10d53e

https://github.com/gamal-daoud/jhotdraw/commit/8d1e95caef518275255b3ec33bfd30d3c13f72ba

https://github.com/gamal-daoud/jhotdraw/commit/



## Fusion de classes : `ChopBezierConnector` et `ChopTriangleConnector` → `ChopDelegatingConnector`

### Problème initial

Le package `org.jhotdraw.draw.connector` contenait plusieurs connecteurs de type « Chop » (qui calculent le point de connexion à la frontière d'une figure). Parmi eux, `ChopBezierConnector` et `ChopTriangleConnector` étaient deux classes **structurellement identiques** :

```java
// ChopBezierConnector
@Override
protected Point2D.Double chop(Figure target, Point2D.Double from) {
    BezierFigure bf = (BezierFigure) getConnectorTarget(target);
    return bf.chop(from);
}

// ChopTriangleConnector
@Override
protected Point2D.Double chop(Figure target, Point2D.Double from) {
    TriangleFigure bf = (TriangleFigure) getConnectorTarget(target);
    return bf.chop(from);
}
```

Ces deux classes ne diffèrent que par le **cast** de la figure cible. Leur existence séparée viole le principe DRY et force la répétition d'un même patron pour chaque figure ayant une méthode `chop()` spécifique.

### Solution : fusion dans `ChopDelegatingConnector`

Une nouvelle classe `ChopDelegatingConnector` a été créée. Elle étend `ChopRectangleConnector` (comme les deux classes d'origine) et accepte en paramètre une `BiFunction<Figure, Point2D.Double, Point2D.Double>` — c'est-à-dire une **stratégie de chop** injectable.

```java
// Utilisation pour une BezierFigure — remplace ChopBezierConnector
new ChopDelegatingConnector(
    bezierFigure,
    (target, from) -> ((BezierFigure) target).chop(from)
);

// Utilisation pour une TriangleFigure — remplace ChopTriangleConnector
new ChopDelegatingConnector(
    triangleFigure,
    (target, from) -> ((TriangleFigure) target).chop(from)
);
```

Les classes `ChopBezierConnector` et `ChopTriangleConnector` sont conservées comme **façades dépréciées** (`@Deprecated`) pour assurer la compatibilité binaire avec le code existant, mais leur corps délègue entièrement à `ChopDelegatingConnector`.

### Comparaison avant / après
-----------------------------------------------------------------------------------------------------
|                         | Avant                          | Après                                  |
|-------------------------|--------------------------------|----------------------------------------|
| Nombre de classes       | 2 classes quasi-identiques     | 1 classe + 2 façades dépréciées        |
| Lignes of codes         | ~469 lignes                    | 495                                    |
| Extensibilité           | Nécessite une nouvelle classe par type de figure | Une lambda suffit    |
| Pattern appliqué        | Aucun                          | **Strategy** (via `BiFunction`)        |
-----------------------------------------------------------------------------------------------------


### Fichiers modifiés / créés
--------------------------------------------------------------------------------------------------------------------
| Fichier                                   | Action                                                               |
|-------------------------------------------|----------------------------------------------------------------------|
| `connector/ChopDelegatingConnector.java`  | **[NOUVEAU]** Classe générique fusionnant la logique des deux connecteurs                                                                                                        |
| `connector/ChopBezierConnector.java`      | Conservé pour compatibilité (`@Deprecated`)                          |
| `connector/ChopTriangleConnector.java`    | Conservé pour compatibilité (`@Deprecated`)                          |
--------------------------------------------------------------------------------------------------------------------

### Bénéfices

- **Élimination de la duplication** : le patron de délégation n'existe plus qu'à un seul endroit.
- **Respect du principe Open/Closed** : pour connecter un nouveau type de figure (ex. `DiamondFigure`), aucune nouvelle sous-classe de connecteur n'est nécessaire — une simple lambda suffit.
- **Application du pattern Strategy** : la logique de chop est interchangeable à l'exécution.
- **Réduction du nombre de classes** dans le package `connector`, ce qui améliore la lisibilité globale du projet.

Lien
- Commit : lien vers le commit
https://github.com/gamal-daoud/jhotdraw/commit/
