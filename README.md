<br />
<br />
<p align="center">
  <a href="https://github.com/ahmedr2001/Search_Engine_Bolt">
    <img src="bolt/src/assets/Vector.png" alt="Logo" width="125" height="125">
  </a>
  
  <h3 align="center">:zap: Bolt :zap:</h3>

  <p align="center">
    Fully working search engine which can search by text or voice for webpages, it also has its own crawler, indexer, ranker that implements Google's Page Rank algorithm, query engine and UI
  </p>
</p>

</div>

<div align="center">

[![GitHub issues](https://img.shields.io/github/contributors/ahmedr2001/Search_Engine_Bolt)](https://github.com/ahmedr2001/Search_Engine_Bolt/contributors)
[![GitHub issues](https://img.shields.io/github/issues/ahmedr2001/Search_Engine_Bolt)](https://github.com/ahmedr2001/Search_Engine_Bolt/issues)
[![GitHub forks](https://img.shields.io/github/forks/ahmedr2001/Search_Engine_Bolt)](https://github.com/ahmedr2001/Search_Engine_Bolt/network)
[![GitHub stars](https://img.shields.io/github/stars/ahmedr2001/Search_Engine_Bolt)](https://github.com/ahmedr2001/Search_Engine_Bolt/stargazers)
[![GitHub license](https://img.shields.io/github/license/ahmedr2001/Search_Engine_Bolt)](https://github.com/ahmedr2001/Search_Engine_Bolt/blob/main/LICENSE)

</div>

# :construction_worker: Dependencies

- [java](https://www.java.com)
- [javac](https://docs.oracle.com/javase/8/docs/technotes/tools/windows/javac.html)
- [mongodb-driver-sync](https://www.mongodb.com/docs/drivers/java/sync/current/)
- [Nodejs](https://nodejs.org)
- [npm](https://www.npmjs.com)
- [vite](https://vitejs.dev/)
- [Spring Boot](https://spring.io/projects/spring-boot/)
- [Java Servlet](https://docs.oracle.com/javaee/5/tutorial/doc/bnafe.html)

# :package: Libraries

- [jsoup](https://jsoup.org)
- [porter-stemmer](https://mvnrepository.com/artifact/ca.rmen/porter-stemmer/1.0.0)

# :pencil: Modules

- [Crawler](/BackEnd/Bolt/src/main/java/Crawler)
- [Indexer](/BackEnd/Bolt/src/main/java/Indexer)
- Ranker
  - [Page Rank Algorithm](/BackEnd/Bolt/src/main/java/PageRankAlgorithm)
  - [Main Ranker](</BackEnd/Spring Boot Application/src/main/java/com/bolt/Brain/Ranker>)
- [Query Engine](</BackEnd/Spring Boot Application/src/main/java/com/bolt/Brain/QueryProcessor>)
- [DataBase Manager](/BackEnd/Bolt/src/main/java/DB)
- [UI](/bolt)
- [Backend](</BackEnd/Spring Boot Application/src/main/java/com/bolt/SpringBoot>)

# :rocket: Running Main Modules

1. Clone using vesion control using any IDE ,e.g: [IntelliJ IDEA](https://www.jetbrains.com/help/idea/set-up-a-git-repository.html)
2. [Install mongodb](https://www.mongodb.com/docs/manual/installation/)
3. [Install mongodb compass](https://www.mongodb.com/docs/compass/master/install/)
4. Connect to `mongodb://localhost:27017`
5. Run [Crawler](/BackEnd/Bolt/src/main/java/Crawler/Main.java)
6. Run [Indexer](/BackEnd/Bolt/src/main/java/Indexer/MainIndexer.java)
7. Run [Page Rank Algorithm](/BackEnd/Bolt/src/main/java/PageRankAlgorithm/PageRankAlgorithm.java)

# :rocket: Running Web App
1. Run [Backend](</BackEnd/Spring Boot Application/src/main/java/com/bolt/SpringBoot/Root.java>)
2. Run Frontend
    - Go to `/bolt` then open a terminal and type `npm run dev`
3. Go to `localhost:5173`

# :fire: Bolt On The Action

## Search By Text

![search_by_text](https://github.com/ahmedr2001/Search_Engine_Bolt/assets/77215230/29b58125-7d7c-4a7c-93f4-72717a982112)


## Navigate Result Pages

![Navigate Result Pages](https://github.com/ahmedr2001/Search_Engine_Bolt/assets/77215230/112bec77-1d6b-4a68-9341-7b6d4b605a88)


## Search By Voice

![Navigate Result Pages](https://github.com/ahmedr2001/Search_Engine_Bolt/assets/77215230/784e76b8-8ef6-47a2-a8a7-f144865c0521)


## Themes

![Themes](https://github.com/ahmedr2001/Search_Engine_Bolt/assets/77215230/2be82495-08b4-433a-bfeb-67837b7e32ec)

## Phrase Searching

![phrase searching 1](https://github.com/ahmedr2001/Search_Engine_Bolt/assets/77215230/7a921ff5-b553-4a1a-b17c-165986594817)
![phrase searching 2](https://github.com/ahmedr2001/Search_Engine_Bolt/assets/77215230/d22df332-462e-4d2a-95cb-1e56e28fc1ab)


# :copyright: Developers

<table>
  <tr>
    <td align="center">
    <a href="https://github.com/ahmedr2001" target="_black">
    <img src="https://github.com/ahmedr2001.png" width="150px;" alt="Ahmed Abdelatty"/>
    <br />
    <sub><b>Ahmed Abdelatty</b></sub></a>
    </td>
    <td align="center">
    <a href="https://github.com/AhmedZahran02" target="_black">
    <img src="https://github.com/AhmedZahran02.png" width="150px;" alt="Ahmed Zahran"/>
    <br />
    <sub><b>Ahmed Zahran</b></sub></a>
    </td>
    <td align="center">
    <a href="https://github.com/AhmedOsama198" target="_black">
    <img src="https://github.com/AhmedOsama198.png" width="150px;" alt="Ahmed Osama Helmy"/>
    <br />
    <sub><b>Ahmed Osama Helmy</b></sub></a>
    </td>
    <td align="center">
    <a href="https://github.com/aliaagheisX" target="_black">
    <img src="https://github.com/aliaagheisX.png" width="150px;" alt="Aliaa Gheis"/>
    <br />
    <sub><b>Aliaa Gheis</b></sub></a>
    </td>
    </td>
    </tr>
 </table>
