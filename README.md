![Java CI with Maven](https://github.com/heathfx/TuinityPerPlayerViewDist/workflows/Java%20CI%20with%20Maven/badge.svg)

TuinityPerPlayerViewDistance uses permissions to set per-player un-ticked view-distance that goes above the value configured in paper.yml

### Features

- Permission based max un-ticked view distance

### How to use
- set permission vd.max.<vd> where <vd> is an integer range 4-32 setting the desired un-ticked view distance
- You can additionally use luckperms contexts to define this on a per-world and per-player basis.
- - ex: lp user <some player> permission set vd.max.16 world=world_the_end

### Commands
- svd: lists the send-view-distance and the client-render-distance settings for each player. (permission: vd.admin)