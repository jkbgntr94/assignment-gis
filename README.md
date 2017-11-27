# General course assignment

Build a map-based application, which lets the user see geo-based data on a map and filter/search through it in a meaningfull way. Specify the details and build it in your language of choice. The application should have 3 components:

1. Custom-styled background map, ideally built with [mapbox](http://mapbox.com). Hard-core mode: you can also serve the map tiles yourself using [mapnik](http://mapnik.org/) or similar tool.
2. Local server with [PostGIS](http://postgis.net/) and an API layer that exposes data in a [geojson format](http://geojson.org/).
3. The user-facing application (web, android, ios, your choice..) which calls the API and lets the user see and navigate in the map and shows the geodata. You can (and should) use existing components, such as the Mapbox SDK, or [Leaflet](http://leafletjs.com/).

## Example projects

- Showing nearby landmarks as colored circles, each type of landmark has different circle color and the more interesting the landmark is, the bigger the circle. Landmarks are sorted in a sidebar by distance to the user. It is possible to filter only certain landmark types (e.g., castles).

- Showing bicykle roads on a map. The roads are color-coded based on the road difficulty. The user can see various lists which help her choose an appropriate road, e.g. roads that cross a river, roads that are nearby lakes, roads that pass through multiple countries, etc.

## Data sources

- [Open Street Maps](https://www.openstreetmap.org/)

## My project

Fill in (either in English, or in Slovak):

**Application description**: Aplikácia ponúka používateľovi možnosť zobraziť parky a oddychové oblasti v Bratislave a jej okolí. V rámci tohto zobrazenia poskytuje niekoľko scenárov.

- Defaultné zobrazenie všetkých parkov.
![all](https://github.com/fiit-pdt/jkbgntr94/assignment-gis/All.jpg)

- Zobrarazenie zadaného počtu bodov záujmu v okolí zvoleného parku.
![all](https://github.com/fiit-pdt/jkbgntr94/assignment-gis/Around.jpg)

- Zobrazenie historických pamiatok v parkoch. Tie sú odlíšené podľa počtu pamiatok v parku intenzitou vnútornej farby.
![all](https://github.com/fiit-pdt/jkbgntr94/assignment-gis/Inside.jpg)

- Vyhľadanie zadaného počtu parkov, ktoré obsahujú fontány/ihriská/lavičky(checkbox). Vyhľadáva sa v okolí pridaného Marku.
![all](https://github.com/fiit-pdt/jkbgntr94/assignment-gis/Search.jpg)

- Zobrazenie chodníkov spolu s ich dĺžkou.
![all](https://github.com/fiit-pdt/jkbgntr94/assignment-gis/path.jpg)

Aplikácia sa ovláda pomocou klikania na zvolené tlačidlá, prípadne na park alebo značku. Početnosť sa nastavuje pomocou slideru. Vyhľadávač sa nastavuje pomocou checkboxov.

**Data source**: www.openstreetmap.org

**Technologies used**: Java, HTML, CSS, MapBox
