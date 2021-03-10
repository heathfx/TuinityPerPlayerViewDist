TuinityPerPlayerViewDistance uses permissions to set per-player un-ticked view-distance that goes above the value configured in paper.yml and limits view distance based on player y-level

### Features
- Permission based max un-ticked view distance
- Lists player send view distance and render distance
- Saves server resources for players that are underground with a configurable per-world view-distance limit for players below a configurable y-level
- This plugin ONLY modifies the no-tick-view-distance

### How to use
- set permission vd.max.\<vd\> where \<vd\> is an integer range 4-32 setting the desired un-ticked view distance
- You can additionally use luckperms contexts to define this on a per-world and per-player basis.
  - ex: lp user <some player> permission set vd.max.16 world=world_the_end
- edit the config file to set limits on view distance for players below a certain y-level

### Commands
- svd: lists the send-view-distance and the client-render-distance settings for each player. (permission: vd.admin)

### Default Config
```yaml
# TuinityPerPlayerViewDist
# by HeathFX (https://github.com/heathfx)

#how often in seconds to check the permissions and locations of players
player-check-interval: 5

#per-world settings - save server resources by not sending as many chunks to players that are likely
#to be underground, not able to see very far anyways.
#remove or comment out a world from this list to disable the feature for that world
#remove or comment out the "worlds" section entirely to completely disable this feature
worlds:
    world:
        #restrict the view distance when player is below a certain y level
        reduce-view-distance-below-y: 60
        view-distance: 5
    world_nether:
        #restrict the view distance when player is below a certain y level
        reduce-view-distance-below-y: 30
        view-distance: 4
```