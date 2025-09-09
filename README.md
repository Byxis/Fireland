# Fireland - Plugin Minecraft pour Serveur Survie

![Fireland Logo](server-icon.svg)

## À propos du projet

Fireland est un plugin Minecraft développé par Byxis_ pour le serveur Fireland. Ce plugin est actuellement en version 4.0 et ajoute de nombreuses fonctionnalités pour enrichir l'expérience de jeu en mode survie.

Le serveur Fireland tourne sous la version 1.19.4 de Minecraft et nécessite Optifine pour charger correctement les textures.
Adresse IP du serveur: `fireland.minesr.com`

## Fonctionnalités principales

### Système de factions
- **Création et gestion de factions**: Les joueurs peuvent créer leur propre faction avec la commande `/faction create <nom>`. Chaque faction a un chef qui peut inviter d'autres joueurs, gérer les rangs des membres, et définir les paramètres de la faction.
- **Territoires contrôlés**: Les factions peuvent revendiquer et contrôler des territoires. Ces zones sont protégées contre les joueurs extérieurs à la faction.
- **Système de bunkers**: Chaque faction peut posséder un bunker qui sert de base sécurisée. Les bunkers peuvent être améliorés pour obtenir plus d'espace et de fonctionnalités.
- **Système d'essaims**: Les factions peuvent organiser des expéditions d'essaim pour combattre des vagues de monstres et obtenir des récompenses (jetons, items rares).
- **Zones spéciales**: Des zones de capture peuvent être contrôlées par les factions, offrant des bonus et des ressources supplémentaires.
- **Économie de faction**: Chaque faction dispose d'un compte bancaire commun et d'un stockage partagé pour les ressources.
- **PvP entre factions**: Système de combat entre factions avec des règles spécifiques et des récompenses pour les vainqueurs.

### Économie
- **Système monétaire**: Basé sur Vault, permet aux joueurs d'acheter et vendre des items, services et propriétés.
- **Jetons**: Monnaie secondaire utilisée pour des achats spéciaux, des améliorations et comme récompense pour certaines activités. Géré via la commande `/jeton`.
- **Factures**: Système permettant aux joueurs d'envoyer des factures à d'autres joueurs pour des services rendus.
- **Banques**: Les joueurs peuvent déposer leur argent dans des banques pour le sécuriser. Les banques peuvent être améliorées pour augmenter leur capacité de stockage (jusqu'au niveau 7). Accessible via la commande `/bank`.

### Joueur
- **Ateliers (workshops)**: Système de crafting avancé permettant aux joueurs de fabriquer des items spéciaux avec des ressources comme la ferraille, la poudre à canon et les médicaments. Les joueurs peuvent apprendre de nouvelles recettes et utiliser un recycleur pour récupérer des matériaux. Accessible via la commande `/workshop` ou `/ws`.
- **Boutiques**: Différents types de boutiques sont disponibles pour acheter des armes (revolver, smg, fusil, assaut, lourd), des objets utilitaires, et des laissez-passer. L'accès à certaines boutiques est limité par le niveau du joueur. Accessible via la commande `/shop <type>`.
- **Système de soif**: Les joueurs ont une barre de soif représentée par leur barre d'expérience. La soif diminue avec le temps et les activités. Les joueurs peuvent boire de l'eau pour restaurer leur soif. Si la soif atteint 0, le joueur commence à subir des dégâts.
- **Système d'infection**: Les joueurs peuvent être infectés par diverses sources (spores, attaques). L'infection a plusieurs niveaux de gravité et cause des dégâts progressifs. Les joueurs peuvent se soigner avec des seringues ou via la commande `/cure`.
- **Sacs à dos**: Permet aux joueurs d'augmenter leur capacité d'inventaire.
- **Boosters**: Avantages temporaires que les joueurs peuvent acheter avec des jetons pour augmenter leurs gains d'XP, d'argent, etc.

### Autres
- **Système de rangs**: Hiérarchie de rangs avec des permissions et avantages spécifiques.
- **Commandes administratives**: Outils pour les administrateurs pour gérer le serveur et les joueurs.
- **Gestion des événements**: Système pour organiser et gérer des événements spéciaux sur le serveur.
- **Redémarrage automatique**: Le serveur redémarre automatiquement à intervalles réguliers pour maintenir les performances.

## Vidéos démonstratives

### Système de factions
<!-- Insérer ici une vidéo démonstrative du système de factions -->
[![Démonstration du système de factions](https://img.youtube.com/vi/VIDEO_ID/0.jpg)](https://www.youtube.com/watch?v=VIDEO_ID)

Cette vidéo montre comment:
- Créer une faction avec `/faction create <nom>`
- Inviter des joueurs avec `/faction invite <joueur>`
- Gérer les territoires avec `/faction claim` et `/faction unclaim`
- Utiliser les bunkers avec `/bunker`
- Organiser des expéditions d'essaim avec `/essaim create`
- Capturer des zones spéciales

### Économie et boutiques
<!-- Insérer ici une vidéo démonstrative du système économique -->
[![Démonstration du système économique](https://img.youtube.com/vi/VIDEO_ID/0.jpg)](https://www.youtube.com/watch?v=VIDEO_ID)

Cette vidéo montre comment:
- Utiliser le système monétaire pour acheter et vendre
- Gérer vos jetons avec `/jeton`
- Envoyer des factures avec `/facture`
- Utiliser la banque avec `/bank`
- Accéder aux différentes boutiques avec `/shop <type>`
- Obtenir des réductions selon votre niveau et faction

### Ateliers et crafting
<!-- Insérer ici une vidéo démonstrative des ateliers -->
[![Démonstration des ateliers](https://img.youtube.com/vi/VIDEO_ID/0.jpg)](https://www.youtube.com/watch?v=VIDEO_ID)

Cette vidéo montre comment:
- Accéder à l'interface des ateliers avec `/workshop gui` ou `/ws gui`
- Apprendre de nouvelles recettes
- Fabriquer des items spéciaux
- Utiliser le recycleur avec `/workshop recycler`
- Gérer vos ressources (ferraille, poudre à canon, médicaments)

## Installation

### Prérequis
- Serveur Minecraft 1.19.4
- Java 17 ou supérieur
- Minimum 4GB de RAM recommandé
- Dépendances:
  - Vault (pour le système économique)
  - ProtocolLib (pour les effets visuels et interfaces personnalisées)
  - WorldGuard (pour la protection des territoires)
  - WGRegionEvents (pour les événements liés aux régions)

### Étapes d'installation
1. Téléchargez la dernière version du plugin depuis [le site officiel](https://fireland.fr/downloads)
2. Assurez-vous que toutes les dépendances sont installées
3. Placez le fichier `Fireland-4.0.jar` dans le dossier `plugins` de votre serveur
4. Redémarrez votre serveur
5. Les fichiers de configuration seront générés automatiquement
6. Modifiez les fichiers de configuration selon vos besoins
7. Redémarrez à nouveau pour appliquer les changements

## Commandes principales

### Commandes générales
| Commande | Description | Permission | Exemples |
|----------|-------------|------------|----------|
| `/fireland` | Gestion du plugin | fireland.admin | `/fireland reload`, `/fireland version` |
| `/speedfly` (alias: `/sf`) | Change la vitesse de vol | fireland.command.flyspeed | `/sf 2` |
| `/heliport` (alias: `/menu`) | Ouvre la carte et permet de se téléporter | fireland.command.heliport | `/heliport` |
| `/discord` | Affiche le lien du Discord officiel | - | `/discord` |

### Factions et territoires
| Commande | Description | Permission | Exemples |
|----------|-------------|------------|----------|
| `/faction` (alias: `/f`, `/fac`) | Commandes pour les factions | - | `/f create Survivants`, `/f invite joueur` |
| `/bunker` | Permet de rejoindre les bunkers | - | `/bunker` |
| `/essaim` | Commandes pour l'essaim | fireland.command.essaim | `/essaim create`, `/essaim join` |
| `/fnote` | Permet de modifier les notes de faction | fireland.command.notes | `/fnote add Note importante` |

### Économie
| Commande | Description | Permission | Exemples |
|----------|-------------|------------|----------|
| `/shop` | Ouvre les différentes boutiques | fireland.command.shop | `/shop utilitaire`, `/shop assaut` |
| `/bank` | Ouvre la banque | fireland.command.bank | `/bank` |
| `/jeton` (alias: `/jt`, `/jetons`) | Commandes pour les jetons | - | `/jeton give joueur 10`, `/jeton balance` |
| `/facture` | Gère les factures | fireland.command.facture | `/facture create joueur 100 Service` |

### Joueur
| Commande | Description | Permission | Exemples |
|----------|-------------|------------|----------|
| `/workshop` (alias: `/ws`) | Commandes pour les ateliers | - | `/ws gui`, `/ws recycler` |
| `/thirst` | Modifie la barre de soif | fireland.command.thirst | `/thirst 100`, `/thirst joueur 50` |
| `/cure` | Enlève l'infection | fireland.command.cure | `/cure`, `/cure joueur` |
| `/infect` | Ajoute une infection | fireland.command.infect | `/infect joueur` |
| `/backpack` | Donne un sac à dos | fireland.command.backpack | `/backpack` |
| `/booster` | Gère les boosters | fireland.command.booster | `/booster create xp 2 1h` |
| `/level` (alias: `/lvl`) | Commandes pour les niveaux | - | `/level info` |

### Administration
| Commande | Description | Permission | Exemples |
|----------|-------------|------------|----------|
| `/rank` | Gère les messages de rangs | fireland.admin | `/rank set joueur message` |
| `/rename` | Renomme l'item en main | fireland.command.Rename | `/rename &6Item Spécial` |
| `/stack` | Stack les items | fireland.command.stack | `/stack` |
| `/n` (alias: `/nv`) | Active/désactive la vision nocturne | fireland.command.n | `/n` |
| `/frestart` (alias: `/restart`) | Redémarre le serveur | fireland.admin | `/restart 5m` |

Pour une liste complète des commandes et leurs options, consultez le fichier `plugin.yml`.

## Configuration

Le plugin utilise plusieurs fichiers de configuration pour stocker les données et paramètres:

### Fichiers principaux
- `config.yml` - Configuration principale du plugin
  ```yaml
  # Exemple de configuration
  server:
    name: "Fireland"
    restart-interval: 12h
    max-players-per-faction: 8
  ```

- `playerdb.yml` - Base de données des joueurs (soif, infection, niveaux, etc.)
  ```yaml
  # Structure exemple
  thirst:
    uuid-du-joueur: 100
  infection:
    uuid-du-joueur: 0
  ```

- `factiondb.yml` - Données des factions (membres, territoires, améliorations)
  ```yaml
  # Structure exemple
  factions:
    Survivants:
      leader: uuid-du-chef
      members:
        - uuid-membre-1
        - uuid-membre-2
      level: 3
      money: 5000
  ```

- `success.yml` - Succès et récompenses
- `jetonsdb.yml` - Données des jetons des joueurs

### Configuration avancée
Pour des configurations plus avancées, vous pouvez modifier:
- Les taux de drop des ressources
- Les prix dans les boutiques
- Les niveaux d'accès aux fonctionnalités
- Les paramètres de l'infection et de la soif
- Les zones spéciales et leurs récompenses

## Développement

### Technologies utilisées
- Java 17
- Spigot/Bukkit API 1.19.4
- Vault API (économie)
- ProtocolLib (packets personnalisés)
- WorldGuard API (protection des territoires)
- MySQL (stockage de données)

### Structure du code
Le plugin est organisé en plusieurs packages:
- `fr.byxis.fireland` - Classes principales
- `fr.byxis.faction` - Système de factions, bunkers et essaims
- `fr.byxis.player` - Fonctionnalités liées aux joueurs
- `fr.byxis.event` - Gestionnaires d'événements
- `fr.byxis.jeton` - Système de jetons
- `fr.byxis.db` - Gestion de la base de données

## Crédits

- Développeur principal: Byxis_
- Équipe de développement: Équipe Fireland
- Testeurs: Communauté Fireland
- Serveur: Fireland (version 1.19.4)

## Liens et ressources

- Discord: [Rejoindre le Discord officiel de Fireland](https://discord.gg/fireland)
  - Canaux d'aide et de support
  - Annonces des mises à jour
  - Signalement de bugs
  - Suggestions de fonctionnalités
- Site web: [Site officiel de Fireland](https://fireland.fr)
  - Documentation complète
  - Téléchargements des mises à jour
  - Actualités du serveur
  - Guides et tutoriels

## Licence et conditions d'utilisation

Ce plugin est sous licence propriétaire. Tous droits réservés.
- Interdiction de redistribuer le plugin
- Interdiction de modifier le code source sans autorisation
- Interdiction d'utiliser le plugin à des fins commerciales sans licence
- Pour toute demande d'utilisation commerciale, contactez l'équipe via le Discord officiel
