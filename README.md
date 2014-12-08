Wordjambalaya
=============

My first Android project. It is a learning project that simply allows me to cheat on a word game
named 'jumble' that we have in our local newspaper

The word game works as follows:
A set of four jumbled words is shown. eg:

    leder
    hyduc
    nittac
    blusty

You are supposed to work out what each word could possibly be, then place the letters of the solved
word into boxes. Some of the boxes have circles around them...
So the above gives:

    Ⓔlder
    ⒹucⒽy
    inⓉacⓉ
    ⓈⓊbtly

You then select the circled letters and use them to create a set of words that solves a visual
puzzle.

In this case, "When it was his turn to clean, the room-mate left the house in... [two words, (3, 4)]

The answer is: "the dust"

To use the application, you simply enter the muddled words, the unmuddled words are shown as you
'type'. Then you indicate the circled words by touching them, and tell the application how
many words there are in the answer - and how long they each are.

The application thinks for a while, and then shows you a list of the possible answers.

# Bugs needing to be fixed (coming soon):
- Doesn't rotate: I need to examine what happens in rotation.

# Note:
Contains the Ispell (ver 3.1.20) word list. See app/src/main/res/raw/read_me for conditions.
