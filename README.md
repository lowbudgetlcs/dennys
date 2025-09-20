<a id="readme-top"></a>
<!-- PROJECT SHIELDS -->
<!--
-->
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![project_license][license-shield]][license-url]



<!-- PROJECT LOGO -->
<br />
<div align="center">
   <a href="https://github.com/lowbudgetlcs/dennys">
      <img src="https://i.imgur.com/DvZvfNz.png" alt="Logo" width="80" height="80" />
   </a>
</div>
   
<div align="center">
<h3 align="center">dennys</h3>

  <p align="center">
    The 24/7 Low BudgetLCS pancake service!
    <br />
    <a href="https://github.com/lowbudgetlcs/dennys"><strong>Explore the docs »</strong></a>
    <br />
    <a href="https://github.com/lowbudgetlcs/dennys/issues/new?labels=bug&template=bug-report---.md">Report Bug</a>
    &middot;
    <a href="https://github.com/lowbudgetlcs/dennys/issues/new?labels=enhancement&template=feature-request---.md">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#development-notes">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

This is the main backend service powering all of the Low BudgetLCS's applications. It also makes pancakes!

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Built With

* [![Kotlin][Kotlin]][Kotlin-url]
* [![Postgres][Postgres]][Postgres-url]
* [![Docker][Docker]][Docker-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started

To get a local copy up and running follow these simple example steps.

### Prerequisites

* docker 
* gnumake
* jdk21

If you are using the [Nix Package Manager](https://github.com/NixOS/nix), there is a convenient [shell.nix](./shell.nix) file to install all prereqs.

### Installation

1. Create a developer account with [Riot Games](https://developer.riotgames.com) and provision an API token.
2. Clone the repo
   ```sh
   git clone https://github.com/lowbudgetlcs/dennys.git
   ```
3. Copy the example environment file
   ```sh
   cp .env.example .env
    ```
4. Enter your API key in `.env`
   ```sh
   RIOT_API_TOKEN = 'ENTER YOUR API TOKEN';
   ```
5. Build a local image
   ```sh
   make build
   ```
You can now run the project with `make run`. This will also create, instantiate,
and run a a local postgres database. It also starts an instance of [pgadmin](https://www.pgadmin.org/) 
for local database inspection.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- USAGE EXAMPLES -->
## Development Notes

### EXTREMELY IMPORTANT

To allow for easy API documentation, we have adopted a design-first appraoch to API development. EVERY new API endpoint 
MUST be created via the Swagger Editor. This way, we have a constantly up-to-date openapi.yaml file that can be
imported into Postman AND accessed in production for very easy documentation and development.

The `Makefile` contains many useful commands to build/run this application locally- the following are short descriptions of each.

* `make build` - Builds the application image from source.
* `make debug-build` - Runs a build with plaintext output. This is useful to debug build-time failures.
* `make run` - Starts the application and several dependencies.
* `make test` - Runs tests in docker container.
* `make jooq` - Runs Jooq code generation against an ephemeral postgres database. This MUST be run after every schema change.
* `make stop` - Stops all containers, and removes them. It is a good habit to run this after ctrl-C-ing `make run`.
* `make db` - Starts the database and pgadmin.
* `make swag` - Starts the Swagger Editor, accessible within your browser.
* `make hook` - Installs pre-commit hook, recommended to do first
* `make format` - Manual format (this is done automatically by the pre-commit hook if installed)
* `make check` - Checks lints from detekt and ktlint

Below are example post-match callbacks Riot will send when a tournament game completes:

Game 1:

```
{
  "startTime": 1725325774610,
  "shortCode": "NA04e01-e599e041-7e3e-48fc-bb53-9e3ebb127555",
  "metaData": "{\"gameNum\":1,\"seriesId\":11}",
  "gameId": 5102531894,
  "gameName": "12998ced-fecc-463b-8c29-73b760109bc4",
  "gameType": "CUSTOM",
  "gameMap": 11,
  "gameMode": "CLASSIC",
  "region": "NA1"
}
```

Game 2:

```
{
  "startTime": 1725328855381,
  "shortCode": "NA04e01-7704f893-f01f-4f1d-be97-cb4468e91a4d",
  "metaData": "{\"gameNum\":2,\"seriesId\":11}",
  "gameId": 5102573449,
  "gameName": "bd35f7a4-7daf-40bb-b9c2-b53e691a368a",
  "gameType": "CUSTOM",
  "gameMap": 11,
  "gameMode": "CLASSIC",
  "region": "NA1"
}
```

Test with your favourite http client (curl, Postman, ...)

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ROADMAP -->
## Roadmap

TODO!

See the [open issues](https://github.com/lowbudgetlcs/dennys/issues) for a full list of proposed features (and known issues).

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Top contributors:

<a href="https://github.com/lowbudgetlcs/dennys/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=lowbudgetlcs/dennys" alt="contrib.rocks image" />
</a>



<!-- LICENSE -->
## License

Distributed under the GNU General Public License. See `LICENSE.txt` for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTACT -->
## Contact

Liam Mackay - [@ruuffian](https://twitter.com/ruuffian) - lblcs.dev@gmail.com

Project Link: [https://github.com/lowbudgetlcs/dennys](https://github.com/lowbudgetlcs/dennys)

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ACKNOWLEDGMENTS -->
## Acknowledgments

These links were useful when developing.

* [Ktor Documentation](https://ktor.io/docs/welcome.html)
* [Riot API Documentation](https://developer.riotgames.com/apis)
* [Riot Tournament Game Policies](https://developer.riotgames.com/docs/lol#tournament-api)
* [Jooq documentation](https://www.jooq.org/doc/latest/manual/sql-execution/)
* [Kotest documentation](https://kotest.io/docs/quickstart)
* [Swagger documentation](https://swagger.io/docs/specification/v3_0/basic-structure/)

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/lowbudgetlcs/dennys.svg?style=for-the-badge
[contributors-url]: https://github.com/lowbidgetlcs/dennys/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/lowbudgetlcs/dennys.svg?style=for-the-badge
[forks-url]: https://github.com/lowbudgetlcs/dennys/network/members
[stars-shield]: https://img.shields.io/github/stars/lowbudgetlcs/dennys.svg?style=for-the-badge
[stars-url]: https://github.com/lowbudgetlcs/dennys/stargazers
[issues-shield]: https://img.shields.io/github/issues/lowbudgetlcs/dennys.svg?style=for-the-badge
[issues-url]: https://github.com/lowbudgetlcs/dennys/issues
[license-shield]: https://img.shields.io/github/license/lowbudgetlcs/dennys.svg?style=for-the-badge
[license-url]: https://github.com/lowbudgetlcs/dennys/blob/main/LICENSE.txt
[Kotlin]: https://img.shields.io/badge/Kotlin-%237F52FF.svg?logo=kotlin&logoColor=white
[Kotlin-url]: https://kotlinlang.org/
[Postgres]: https://img.shields.io/badge/Postgres-%23316192.svg?logo=postgresql&logoColor=white
[Postgres-url]: https://www.postgresql.org/
[Docker]: https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=fff
[Docker-url]: https://docker.com
