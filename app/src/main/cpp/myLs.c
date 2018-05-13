#include<stdio.h>
#include<errno.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include<string.h>
#include <stdlib.h>
#include <dirent.h>
#include <sys/vfs.h>

typedef int bool;
#define true 1
#define false 0

__mode_t mode;
const char *delim="#@$";
char * PATH;
int error;
bool search;
char* NAME;

/*
    itemType
        1:regular file
        2:directory
        3:symLink
        4:symLink points to regular file
        5:symLink points to directory
*/

int itemType;
char *n1=".";
char *n2="..";
int count=0,count2=0;
struct stat mystat;
char* name;
char permission[9];
long long sizeinBytes,timeinSec;






void printer(char path[512])//print 6 values
{
    printf("%d%s%s%s%s%s%lld%s%lld%s%s\n",itemType,delim,permission,delim,name,delim,sizeinBytes,delim,timeinSec,delim,path);
}

void printError(char msg[100],int errorCode)//print 2 values
{
	printf("%s%s%d\n",msg,delim,errorCode);
}
void printOneFile()//print 4 values
{	printf("%d%s%s%s%lld%s%lld\n",itemType,delim,permission,delim,sizeinBytes,delim,timeinSec);
}
void printAvailable(long long bytes)//print 1 value
{
	printf("%lld\n",bytes);
}


/*
This sets the permission
*/
void sperm()
{
     // user permissions
    if ((mode & S_IRUSR) == S_IRUSR)
    permission[0] = 'r';
    else
    permission[0] = '-';

    if ((mode & S_IWUSR) == S_IWUSR)
    permission[1] = 'w';
    else
    permission[1] = '-';

    if ((mode & S_IXUSR) == S_IXUSR)
    permission[2] = 'x';
    else
    permission[2] = '-';

    // group permissions
    if ((mode & S_IRGRP) == S_IRGRP)
    permission[3] = 'r';
    else
    permission[3] = '-';

    if ((mode & S_IWGRP) == S_IWGRP)
    permission[4] = 'w';
    else
    permission[4] = '-';

    if ((mode & S_IXGRP) == S_IXGRP)
    permission[5] = 'x';
    else
    permission[5] = '-';

    // other permissions
    if ((mode & S_IROTH) == S_IROTH)
    permission[6] = 'r';
    else
    permission[6] = '-';

    if((mode & S_IWOTH) == S_IWOTH)
    permission[7] = 'w';
    else
    permission[7] = '-';

    if((mode & S_IXOTH) == S_IXOTH)
    permission[8] = 'x';
    else
    permission[8] = '-';

}



/*
returns
        1:regular file
        2:directory
        3:symLink
*/
int getItemType()
{
    if(S_ISLNK(mode))
    return 3;
    if(S_ISDIR(mode))
    return 2;
    if(S_ISREG(mode))
    return 1;

    return 0;
}



void R(const char *folderPath)
{
    char path[512];
    count++;

    DIR *mydir;
    struct dirent *myfile;

    mydir=opendir(folderPath);

    if(mydir==NULL)
    return;
    bool appendSlash=folderPath[strlen(folderPath)-1]!='/';

    while((myfile = readdir(mydir)) != NULL)
     {
        name=myfile->d_name;

        if(strcmp(n1,name)==0 || strcmp(n2,name)==0)
        continue;

        if(appendSlash)
        sprintf(path, "%s/%s",folderPath,name);
        else
        sprintf(path, "%s%s",folderPath,name);


		if(lstat(path,&mystat)==-1)//not following the link
        {
			printError("Error in reading a file:",47);
            continue;
        }

        mode=mystat.st_mode;
        sperm();//sets permission
        itemType=getItemType();

        if(itemType==0)
            continue;

        if(itemType==3)//follow the symlink
        {
            if(stat(path, &mystat)==-1)
            {
				printError("Failed to follow symLink:",47);
                continue;
            }

            mode=mystat.st_mode;
            itemType+=getItemType();
            if(itemType!=4 && itemType!=5)
                continue;
        }

        timeinSec=mystat.st_mtime;
        sizeinBytes=mystat.st_size;

        if(search && strcasestr(name,NAME)==NULL)
        {
            //DONT PRINT
        }
        else
        {
           printer(path);
        }


        if(itemType==2 || itemType==5)
        {
           R(path);
        }
        else
        {
            count2++;
        }
    }

    closedir(mydir);
}

void list()
{
    char path[512];
    DIR *mydir;
    struct dirent *myfile;

    mydir=opendir(PATH);

    if(mydir==NULL)
    {
        printError(strerror(errno),errno);
        error=-1;
        return ;
    }

	bool appendSlash=PATH[strlen(PATH)-1]!='/';

    while((myfile = readdir(mydir)) != NULL)
     {
        name=myfile->d_name;
        if(strcmp(n1,name)==0 || strcmp(n2,name)==0)
        continue;

        if(appendSlash)
        sprintf(path, "%s/%s",PATH,name);
        else
        sprintf(path, "%s%s",PATH,name);

        if(lstat(path,&mystat)==-1)//not following the link
        {
			printError("Error in reading a file:",47);
            continue;
        }

        mode=mystat.st_mode;
        sperm();//sets permission
        itemType=getItemType();

        if(itemType==0)
            continue;

        if(itemType==3)//follow the symlink
        {
            if(stat(path, &mystat)==-1)
            {
				printError(strerror(errno),47);
                continue;
            }

            mode=mystat.st_mode;
            itemType+=getItemType();
            if(itemType!=4 && itemType!=5)
                continue;
        }

        timeinSec=mystat.st_mtime;
        sizeinBytes=mystat.st_size;

        printer(path);
        count++;
      }
	  closedir(mydir);

}

void getAvailable()
{
    struct statfs sfs;
    if(statfs(PATH,&sfs)==-1)
    {
        printAvailable(-1);
    }
    else
    {
        long long  x;
        x= sfs.f_bavail*sfs.f_bsize;
        printAvailable(x);
    }
}

void getOneFile()
{
    if(lstat(PATH,&mystat)==-1)
    {
        printError(strerror(errno),errno);
        return;
    }
    mode=mystat.st_mode;
    sperm();//sets permission
    itemType=getItemType();
    if(itemType==0)
    {
        error=-1;
        return;
    }

    if(itemType==3)
    {
      //it is a symlink
       if(stat(PATH, &mystat)==-1)
       {
            printError(strerror(errno),errno);
            return;
       }
       mode=mystat.st_mode;
       itemType+=getItemType();
       if(itemType!=4 && itemType!=5)
       {
            error=-1;
            return;
       }
    }

    timeinSec=mystat.st_mtime;
    sizeinBytes=mystat.st_size;

	printOneFile();
}

char *error1="Invalid 1st parameter ,should be {1,2,3}";
char *error2="Provide correct Parameters";



int main(int argc, char* argv[])
{
    if(argc!=3 && argc!=4)
    {
        printError(error2,47);
        return -1;
    }
    PATH=argv[2];
    int len=strlen(PATH);
    if(len<1)
    {
        printError(error2,47);
        return -1;
    }
    if(strlen(argv[1])!=1)
    {
        printError(error1,47);
        return -1;
    }
    error=0;
    count=0;
    count2=0;
    char param=argv[1][0];

    switch(param)
    {
        case '1':
        count=0;
        list();
        printf("%d\n",count);
        break;

        case '2':
        search=false;
        R(PATH);
        printf("%d#%d\n",count,count2);
        break;

        case '3':
        if(argc!=4)
        {
            printError(error2,47);
            return -1;
        }
        search=true;
        NAME=argv[3];
        R(PATH);
        printf("%d#%d\n",count,count2);
        break;

        case '4':
        getOneFile();
        break;

        case '5':
        getAvailable();
        break;

        default:
        printError(error1,47);
        return -1;
    }

    return error;

}