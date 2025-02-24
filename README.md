<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->
<a id="readme-top"></a>
<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
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
   
<div align="center">
<h3 align="center">dennys</h3>

  <p align="center">
    The 24/7 Low BudgetLCS pancake service!
    <br />
    <a href="https://github.com/lowbudgetlcs/dennys"><strong>Explore the docs »</strong></a>
    <br />
    &middot;
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
* jdk17
* yq

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
5. Build a local application image
   ```sh
   make build
   ```
6. Generate the sql migrations for a local database
    ```sh
    make migrations
    ```
7. Change git remote url to avoid accidental pushes to base project
   ```sh
   git remote set-url origin github_username/repo_name
   git remote -v # confirm the changes
   ```
You can now run the project with `make run`. This will create a local postgres database. As well as this, 
`make run-dev` will run a [pgadmin](https://www.pgadmin.org/) instance to easily look at the local database.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- USAGE EXAMPLES -->
## Development Notes

The `Makefile` contains many useful commands to build/run this application locally- the following are short descriptions of each.

* `make migrations` - Generates valid sql files in ./build/resources/migrations for initializing local postgres database. This command must be run each time a schema change occurs.
* `make build` and `make rebuild` - Builds an application image from source. `rebuild` will delete old images.
* `make run` - Runs docker-compose up on the application and its dependencies.
* `make run-dev` - Same as `make run`, but with a pgadmin instance.
* `make test` - Runs tests locally. Does not use Docker.
* `make debug-build` - Runs a build with plaintext output. This is useful to debug build-time failures.
* `make stop` - Stops all containers, and removes them. It is a good habit to run this after ctrl-C-ing `make run`.
* `make drop-db` - Deletes the pgdata volume. Do this to re-run the initialization scripts, usually after a schema change.

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

Your Name - [@ruuffian](https://twitter.com/ruuffian) - lblcs.dev@gmail.com

Project Link: [https://github.com/lowbudgetlcs/dennys](https://github.com/lowbudgetlcs/dennys)

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ACKNOWLEDGMENTS -->
## Acknowledgments

These links were useful when developing.

* [Ktor Documentation](https://ktor.io/docs/home.html)
* [RabbitMQ BASIC Example](https://www.rabbitmq.com/tutorials/tutorial-one-java)
* [SQLDelight Documentation](https://sqldelight.github.io/sqldelight/2.0.2/jvm_postgresql/)
* [Riot API Documentation](https://developer.riotgames.com/apis)
* [Riot Tournament Game Policies](https://developer.riotgames.com/docs/lol#tournament-api)

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