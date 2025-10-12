# Fireland - Plugin Minecraft pour Serveur Survie

![Fireland Logo](server-icon.svg)

## À propos du projet

Fireland est un plugin Minecraft développé par Byxis_ pour le serveur Fireland. Ce plugin est actuellement en version 5.0 et ajoute de nombreuses fonctionnalités pour enrichir l'expérience de jeu sur notre serveur.

Le serveur Fireland tourne sous la version 1.21.4 de Minecraft et nécessite Optifine pour charger correctement les textures. il tourne également sur Fabric, si les mods permettant d'ajouter les features d'Optifine sont installés (ex: ModPack Fabulously Optimised).
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
- **Jetons**: Monnaie secondaire utilisée pour des achats spéciaux, des améliorations et comme récompense pour certaines activités.
- **Factures**: Système permettant de voir les dépenses réalisées utilisant des jetons.
- **Banques**: Les joueurs peuvent déposer leur argent ou des items dans des banques pour le sécuriser. Les banques peuvent être améliorées pour augmenter leur capacité de stockage (jusqu'au niveau 7).

### Joueur
- **Ateliers (workshops)**: Système de crafting avancé permettant aux joueurs de fabriquer des items spéciaux avec des ressources comme la ferraille, la poudre à canon et les médicaments. Les joueurs peuvent apprendre de nouvelles recettes et utiliser un recycleur pour récupérer des matériaux.
- **Boutiques**: Différents types de boutiques sont disponibles pour acheter des armes (revolver, smg, fusil, assaut, lourd), des objets utilitaires, et des laissez-passer. L'accès à certaines boutiques est limité par le niveau du joueur. Accessible via la commande `/shop <type>`.
- **Système de soif**: Les joueurs ont une barre de soif représentée par leur barre d'expérience. La soif diminue avec le temps et les activités. Les joueurs peuvent boire de l'eau propre pour restaurer leur soif. Si la soif atteint 0, le joueur commence à subir des dégâts.
- **Système d'infection**: Les joueurs peuvent être infectés par diverses sources (spores, attaques). L'infection a plusieurs niveaux de gravité et cause des dégâts progressifs. Les joueurs peuvent se soigner avec des seringues. Ils peuvent également s'immuniser contre le virus pour 10 minutes.
- **Sacs à dos**: Permet aux joueurs d'augmenter leur capacité d'inventaire.
- **Boosters**: Avantages temporaires que les joueurs peuvent acheter avec des jetons pour augmenter leurs gains d'XP, d'argent, de loot, etc.

### Autres
- **Système de rangs**: Hiérarchie de rangs avec des permissions et avantages spécifiques.
- **Commandes administratives**: Outils pour les administrateurs pour gérer le serveur et les joueurs.
- **Gestion des événements**: Système pour organiser et gérer des événements spéciaux sur le serveur.
- **Redémarrage**: Disponibilité de la commande /frestart pour annoncer les redémarrages sans surprendre les joueurs.

## Vidéos démonstratives

### Système de factions

(Soon)

[![Démonstration du système de factions](https://img.youtube.com/vi/VIDEO_ID/0.jpg)](https://www.youtube.com/watch?v=VIDEO_ID)

Cette vidéo montre comment:
- Créer une faction avec `/faction create <nom>`
- Inviter des joueurs dans le menu de l'Intendant
- Gérer les territoires capturés dans l'Intendant
- Utiliser les bunkers de faction
- Capturer des zones spéciales

### Économie et boutiques

(Soon)

[![Démonstration du système économique](https://img.youtube.com/vi/VIDEO_ID/0.jpg)](https://www.youtube.com/watch?v=VIDEO_ID)

Cette vidéo montre comment:
- Utiliser le système monétaire pour acheter et vendre
- Gérer vos jetons avec `/jeton`
- Consulter vos factures avec `/facture`
- Utiliser la banque
- Accéder aux différentes boutiques
- Obtenir des réductions selon votre niveau et faction

### Ateliers et crafting

(Soon)

[![Démonstration des ateliers](https://img.youtube.com/vi/VIDEO_ID/0.jpg)](https://www.youtube.com/watch?v=VIDEO_ID)

Cette vidéo montre comment:
- Accéder à l'interface des ateliers
- Gérer vos ressources (ferraille, poudre à canon, médicaments)
- Apprendre de nouvelles recettes
- Fabriquer des items spéciaux
- Utiliser le recycleur

## Commandes principales

### Commandes générales
| Commande | Description | Permission | Exemples |
|----------|-------------|------------|----------|
| `/fireland` | Gestion du plugin | fireland.admin | `/fireland reload`, `/fireland version` |
| `/speedfly` (alias: `/sf`) | Change la vitesse de vol | fireland.command.flyspeed | `/sf 2` |
| `/heliport` (alias: `/menu`) | Ouvre la carte et permet de se téléporter | fireland.command.heliport | `/heliport` |
| `/discord` | Affiche le lien du Discord officiel | - | `/discord` |

### Factions et territoires
| Commande | Description                                                            | Permission | Exemples |
|----------|------------------------------------------------------------------------|------------|----------|
| `/faction` (alias: `/f`, `/fac`) | Commandes pour les factions                                            | - | `/f create Survivants`, `/f invite joueur` |
| `/bunker` | Permet de gérer les bunkers                                            | - | `/bunker` |
| `/essaim` | Commandes pour l'essaim, gérées par les menus plutot que les commandes | fireland.command.essaim | `/essaim create`, `/essaim join` |
| `/fnote` | Permet de modifier les notes placées                                   | fireland.command.notes | `/fnote <text>` |

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

## Développement

### Technologies utilisées
- Java 21
- Paper API 1.12.4
- Vault API (économie)
- ProtocolLib (packets personnalisés)
- WorldGuard API (protection des territoires)
- MySQL (stockage de données)

### Structure du code
Le plugin est organisé en plusieurs packages:
- `fr.byxis.fireland` - Classes principales
- `fr.byxis.faction` - Fonctionnalités liées aux essaims, comme le système de factions, bunkers et essaims
- `fr.byxis.player` - Fonctionnalités liées aux joueurs
- `fr.byxis.event` - Gestionnaires d'événements
- `fr.byxis.jeton` - Système de jetons
- `fr.byxis.db` - Gestion de la base de données

## Crédits

- Développeur: Byxis_
- Artiste: Fyrelix
- Testeurs: Communauté Fireland

## Liens et ressources

- Discord: [Rejoindre le Discord officiel de Fireland](https://discord.gg/fireland)
  - Canaux d'aide et de support
  - Annonces des mises à jour
  - Signalement de bugs
  - Suggestions de fonctionnalités