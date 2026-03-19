## -------------------------------------------------------------------
# jhotdraw projet
# Génie logiciel JHotDraw
## Authors:Gamal Daoud Youssouf
## Groupe: 1
## Partie 2 : Amélioration du projet
## ---------------------------------------------------------------------

## Enseignante : Madame   Imen Sayar  (imen.sayar@univ-lille.fr)
1
`Lien du projet :  https://github.com/gamal-daoud/jhotdraw

## Objectifs de ce projet :
Étudier la qualité logicielle, Amélioration du projet.

## Introduction
Après avoir analysé la qualité logicielle de JHotDraw (jhotdraw-core) dans la première partie, ce rapport présente les améliorations apportées au projet. Les modifications, classées par difficulté, visent à renforcer la cohérence et la maintenabilité du code en se concentrant sur quelques classes clés. Chaque changement est documenté : situation initiale, modification réalisée également amélioration de readme.


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

jhotdraw-core/src/main/java/org/jhotdraw/draw/figure/Attributes.java
https://github.com/wumpz/jhotdraw/commit/e14f513a6a4533465430242c2dd68c1df0363a5e



## 8 Grandes modifications

# Suppression de duplication de code:
les ensembles links des modifications ici
https://github.com/gamal-daoud/jhotdraw/commit/5dd2d608b0319bb09856771ebf39c7000592c1e2

Le problème central était dans les méthodes (drawDrawingVolatileBuffered et drawDrawingNonvolatileBuffered)
contenaient deux blocs identiques:
- Bloc 1
calcul du shift et mise à jour de bufferedArea/dirtyArea

Bloc 2
- effacement et repaint de la zone sale



##  Analyse avant / après — SonarQube:

#### Duplications

| Métrique         | Avant | Après | Gain       |
|------------------|-------|-------|------------|
| Density          | 46.0% | 41.9% | -4.1%      |
| Duplicated Lines | 714   | 634   | -80 lignes |
| Duplicated Blocks| 19    | 17    | -2 blocs   |

Les 2 bloc supprimés correspondent exactement aux deux blocs extraits dans (updateBufferedAreaAndShift et repaintDirtyBufferArea).

#### Complexité

| Métrique             | Avant | Après | Gain |
|----------------------|-------|-------|------|
| Cyclomatic Complexity| 265   | 260   | -5   |
| Cognitive Complexity | 279   | 261   | -18  |

Le gain en Cognitive Complexity (-18) est plus important que le gain en Cyclomatic (-5). C'est logique : la Cognitive Complexity pénalise les structures imbriquées. En extrayant les if/else if chaînés du shift hors des méthodes principales, on réduit la profondeur d'imbrication perçue par le lecteur, ce que SonarQube récompense davantage.

#### Taille et qualité

| Métrique    | Avant     | Après | Gain       |
|-------------|-----------|-------|------------|
| Lines       | ~1 600    | 1 513 | -87 lignes |
| Code Smells | 63        | 54    | -9         |
| Functions   | —         | 94    | +2         |

La réduction de ~87 lignes vient du fait que les deux blocs dupliqués ont été remplacés par un simple appel de méthode chacun. On ajoute 2 fonctions mais on gagne 9 Code Smells — SonarQube considère que le code est maintenant plus lisible et maintenable.

# Ce que les chiffres confirment en expliquant
La duplication était le problème principal. 80 lignes dupliquées sur 1 600 = c'est exactement les deux blocs identiques dans drawDrawingVolatileBuffered et drawDrawingNonvolatileBuffered. L'extraction en méthodes privées les a éliminés proprement.

La complexité cognitive chute plus que la cyclomatique parce que les if/else if du calcul de shift étaient profondément imbriqués dans une boucle while(true) elle-même dans la méthode principale. En les sortant dans updateBufferedAreaAndShift, le niveau d'imbrication maximal des deux méthodes appelantes diminue.

Les Code Smells passent de 63 à 54 (-9) confirme que SonarQube reconnaît directement la suppression de duplication comme une amélioration de maintenabilité, pas seulement un changement cosmétique.

# Principe appliqué et ce qui a été fait :
- Supprimer la duplication : blocs identiques extraits en méthodes updateBufferedAreaAndShift et repaintDirtyBufferArea.
- Décomposer une méthode mixte : BufferUpdateResult sépare proprement retour et effet de bord.
- Réduire la complexité cyclomatique : chaque méthode résultante a moins de branches imbriquées.

