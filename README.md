This project is licensed under the MIT license! See LICENSE.

The ocarina icon was drawn with inspiration from the Ocarina Mod by bobafettlink and pretty much every other presentation of an ocarina that I could find on the internet. The carrot texture was modified from the vanilla carrot texture in colour by me, and then given beautiful animation by AranaiRa. See CREDITS.

I am not responsible for horses stuck in walls, I am not responsible for horses falling to their death in lava. Summon responsibly, because a "safe spot" will not be searched for.

TODO:
* Optionally specify consumable item (i.e., carrot) (item_id:meta) and quantity to consume every time a horse is summoned.
* Optionally specify that the ocarina has a durability which decreases every time a horse is summoned. Additionally, it could be configured to never be destroyed but instead "break" and require repair.
* Optionally specify the material that can be used to repair the ocarina in an anvil, with a default of, say, golden carrots.
* Optionally allow a recipe that crafting the ocarina shapelessly with the repair item repairs one point of damage.
* Optionally specify the material that can be used to repair the enchanted carrot in an anvil, with a default of, say, blocks of gold or emerald.
* Update direction specification in "list" mode of ocarina to include intermediate cardinal directions.
* Blacklist for specific instnances of AbstractHorse that should be ignored.

TO NOT EVER DO:
* Implement any form of unloaded-chunk or cross-dimension capability. It's your responsibility to keep your horses loaded or to get them to and from other dimensions.
