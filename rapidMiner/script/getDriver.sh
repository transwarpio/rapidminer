#!/bin/sh

dir=inceptor-driver

rm -rf $dir
mkdir $dir
cd $dir
mkdir "tmp"

#for hadoop/hive jars
hh=(
"hadoop-annotations-*-transwarp*.jar"
"hadoop-auth-*-transwarp*.jar"
"hadoop-common-*-transwarp*.jar"
"hadoop-hdfs-*-transwarp*.jar"
"hive-common-*-transwarp*.jar"
"hive-jdbc-*-transwarp*.jar"
"hive-metastore-*-transwarp*.jar"
"hive-serde-*-transwarp*.jar"
"hive-service-*-transwarp*.jar"
"hive-shims-*-transwarp*jar"
)

#for general jars
array=(
"commons-cli-*.jar"
"commons-codec-*.jar"
"commons-collections-*.jar"
"commons-configuration-*.jar"
"commons-lang-*.jar"
"commons-logging-*.jar"
"guava-*.jar"
"httpclient-*.jar"
"httpcore-*.jar"
"libfb303-*jar"
"libthrift-*jar"
"log4j-*.jar"
"slf4j-api-*.jar"
"slf4j-log4j12-*.jar"
"jsch-*.jar"
"jzlib-*.jar"
"protobuf-*.jar"
"stringtemplate-*.jar"
)

cp /usr/lib/hive/lib/antlr-*jar ./tmp/

configPrefix="hdfs1"
configs=(
    "/etc/$configPrefix/conf/core-site.xml"
    "/etc/$configPrefix/conf/hdfs-site.xml"
)

for item in ${hh[@]}
do
  files=`find /usr/lib/hive/ /usr/lib/hadoop/ /usr/lib/hadoop-hdfs/ -name "$item"`
  for file in ${files[@]}
  do
#    echo "---------------------------------------------------------"
#    echo "$file"

    if ( [[ $file =~ tests.jar$ ]] || [[ $file =~ test.jar$ ]] );
    then
      continue
    fi
    cmd="cp -f $file ./tmp/"
    $cmd
    echo "$cmd"
  done
done

for item in ${array[@]}
do
#  echo "----------------------------------------------------------------------------------"
  files=`find /usr/lib/hive/ /usr/lib/hadoop/ /usr/lib/hadoop-hdfs/ -name "$item"`
#  echo $files
  maxFile=""
  maxValue=0
  for file in ${files[@]}
  do
#    echo "---------------------------------------------------------"
#    echo $file
    
    if ( [[ $file =~ tests.jar$ ]] || [[ $file =~ test.jar$ ]] );
    then
      break
    fi

    segments=${file//// }
    name=""
    for segment in ${segments[@]}
    do
       name=$segment
    done
#    echo $name

    nums=`echo "$name" |grep -o "[0-9]*"`
    value=0
    for num in ${nums[@]}
    do
      value=$[value*100+num];
    done
#    echo $value

    if [ $maxValue -le $value ];
    then
      maxValue=$value
      maxFile=$file
    fi
  done
#  echo "---------------- $maxFile --------------------"
  
  cmd="cp -f $maxFile ./tmp/"
  $cmd
  echo $cmd
#  echo "----------------------------------------------------------------------------------"
#  echo ""
#  echo ""
done


# copy configs
for conf in ${configs[@]}
do
    cmd="cp -f $conf ./tmp/"
    $cmd
    echo $cmd
done

#
#`cp /usr/lib/hadoop/lib/*.jar ./tmp/`
#

#-----------------------------------------------
#  Check all the jars
#-----------------------------------------------

cd "./tmp/"

success=1
for item in ${array[@]}
do
#  echo $item
  files=`find -name "$item"`
  if [ -z "$files" ];
  then
    echo "    File not found: --> $item"
    success=0
  fi
done

if [ $success -eq 0 ];
then
  echo ""
  echo "    -------------------------------------------------"
  echo "    |    PLEASE MAKE SURE ALL THE FILES IS FOUND!   |"
  echo "    -------------------------------------------------"
  exit 1;
fi


result=`find -name \*.jar -exec jar -xf {} \;`
result=`find -name hive-jdbc-\*.jar -exec jar -xf {} \;`

branch=`grep BuildScmBranch  META-INF/MANIFEST.MF -i`
version=`echo "$branch" |grep -o "transwarp-[0-9]\+\.[0-9]\+\(\.[0-9]\+\)*" |grep -o "[0-9]\+\.[0-9]\+\(\.[0-9]\+\)*"`
echo " version = $version"
echo ""


targetJar=$dir"-"$version".jar"
targetTar=$dir"-"$version".tar"
targetFiles=$dir"-"$version"-files"
if [ -z $version ];
then
  targetJar=$dir".jar"
  targetTar=$dir".tar"
  targetFiles=$dir"-files"
fi

result=`tar cvf ../$targetTar *.jar`

result=`mkdir -p  ../$targetFiles/`

result=`find -name \*.jar |sort  |tee filelist `

result=`mv ./*.jar ../$targetFiles/`

result=`jar cfM ../$targetJar ./`


cd ..
#result=`rm -rf "./tmp/"`


path=`pwd`
echo " All-in-one Driver:"
echo " $path/$targetJar"
echo ""
echo " Separated Driver files:"
echo " $path/$targetTar"
echo ""


cd ..
#rm -rf $dir


