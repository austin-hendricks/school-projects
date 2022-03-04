###### [Back to all projects in school-projects repo](https://github.com/austin-hendricks/school-projects)

# Interactive HTML Glossary Generator
###### Java project completed in Autumn of 2020 to generate interactive HTML based on user input.
###### Assignment for Ohio State University's sophomore-level Computer Science course, "Software II"
###### Author: [Austin Hendricks](https://github.com/austin-hendricks)

## Table of Contents
1. [Description](#description)
2. [How to Use](#how-to-use)
3. [Formatting Input Text](#formatting-input-text)
4. [Original Problem Statement](#original-problem-statement)


## Description
***
This is a Java application for generating a interactive Glosary page based upon given terms and their definitions. Input requires a specially formatted text file. Output 
will be a set of HTML files, with a top-level index listing each term in the glossary, as well as separate pages for each of the terms and their definitions. 

Clicking on a term in the index shall take you to the page 
for that term and its associated definition. Moreover, clicking on any term in the glossary that happens to appear in a definition shall take you to the page for _that_ term 
and its associated definition.

Sample outputs are located within the `/output` folder.


## How to Use
***
1. Clone this repository using `git clone https://github.com/austin-hendricks/school-projects.git`
2. Navigate to `school-projects/Glossary` in a command line environment.
3. Prepare the text file to be used for program input and copy path to that file.
    * Note: sample input files located under `/data` folder.
4. After making sure Java is installed on your machine, issue the following command:
```
$ java -jar Glossary.jar
```
5. Follow the programs prompts and, once complete, locate the program's output under the `/output` directory.



## Formatting Input Text
***
The input must consist of a single term on the first line, its entire definition on the next line (terminated by an empty line), another term on the next line, its definition on the next one (terminated by an empty line), and so on. 

The input file must continue in this fashion through the definition of the last term, which must end with its terminating empty line (thus, total of two empty lines after final definition). The program will not check for invalid input; the user is responsible for providing input that meets the stated conditions.

See `data/terms.txt` as an example of properly formatted input.


## Original Problem Statement
***
The original problem statement for this project, issued by The Ohio State University through the course *CSE 2221 - Software 1*, can be viewed 
[here](http://web.cse.ohio-state.edu/software/2221/web-sw1/assignments/projects/glossary/glossary.html).

My changes after project submission:
* Replaced use of OSU's "components" library with standard Java components.
* Output folder specified by user no longer has to already exist. The program will create the output folder with its user-specified name under the project's `/output` directory.
* Modified output style and path for maximum portability (user can now copy their output folder and it will have everything they need for their glossary).
* Added ability to recognize and handle terms in their plural form.
     * For example, if *color* is a term, and the word "colors" appears somewhere else in the glossary, the program will recognize "colors" as being the plural version of *color* and will generate links accordingly.

Grade recieved for this project: 
### A+ 
(12/10 possible points)
