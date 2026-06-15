# SkyResources KubeJS

SkyResources registers its data-driven type registries with KubeJS when KubeJS is installed.
Scripts may add or override entries from `kubejs/server_scripts/*.js` through KubeJS registry events.

## Registry Events

```js
ServerEvents.registry('skyresources:casing_type', event => {
  event.create('kubejs:copper').json({
    translation_key: 'block.kubejs.copper_machine_casing',
    texture: 'minecraft:block/copper_block',
    max_heat: 1200,
    efficiency: 1.1,
    structure_rule: 'metal'
  })
})

ServerEvents.registry('skyresources:combustion_heater_type', event => {
  event.create('kubejs:copper').json({
    translation_key: 'block.kubejs.copper_combustion_heater',
    body_texture: 'minecraft:block/copper_block',
    top_texture: 'skyresources:block/combustion',
    speed: 1.1,
    efficiency: 1.1,
    structure_rule: 'metal',
    fuel: {
      kind: 'furnace'
    }
  })
})

ServerEvents.registry('skyresources:heat_provider_type', event => {
  event.create('kubejs:copper').json({
    translation_key: 'block.kubejs.copper_heat_provider',
    texture: 'minecraft:block/copper_block',
    speed: 1.1,
    efficiency: 1.1,
    fuel: {
      kind: 'item',
      item: 'minecraft:coal',
      rate: 1600
    }
  })
})

ServerEvents.registry('skyresources:condenser_type', event => {
  event.create('kubejs:copper').json({
    translation_key: 'block.kubejs.copper_condenser',
    texture: 'minecraft:block/copper_block',
    speed: 1.1,
    efficiency: 1.1
  })
})

ServerEvents.registry('skyresources:ore_alchemy_dust_type', event => {
  event.create('kubejs:osmium').json({
    source_tag: 'c:ores/osmium',
    rarity: 6,
    color: '#7FAFC6'
  })
})

ServerEvents.registry('skyresources:dirty_gem_type', event => {
  event.create('kubejs:ruby').json({
    source_tag: 'c:gems/ruby',
    rarity: 0.015,
    color: '#FA1E1E'
  })
})
```

The event JSON uses the same fields as the data-pack files under
`data/<namespace>/skyresources/<registry_name>/<id>.json`.

## Process Recipes

Normal SkyResources process recipes remain recipe data and can be changed with KubeJS custom recipes:

```js
ServerEvents.recipes(event => {
  event.custom({
    type: 'skyresources:process',
    process: 'combustion',
    inputs: [
      { ingredient: 'minecraft:gunpowder', count: 1 }
    ],
    outputs: [
      { id: 'minecraft:blaze_powder', count: 1 }
    ],
    parameter: 150
  })
})
```
