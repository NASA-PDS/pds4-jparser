#!/bin/bash

usage() {
    echo "$(basename $0) <dev_or_ops> <IM version>"
    echo "     dev_or_ops - still in dev or released"
    echo "     IM Version - e.g. 1D00"
    echo
    exit 1
}

if [ $# -ne 2 ]; then
    usage
fi

if [ $1 == "dev" ]; then
    BASE_URL=https://pds.jpl.nasa.gov/datastandards/schema/develop/pds/
elif [ $1 == "ops" ]; then
    BASE_URL=https://pds.jpl.nasa.gov/pds4/pds/v1/
else
    usage
fi

version=$2
outdir=src/build/resources/schema/$version/

mkdir -p $outdir
wget --no-parent -P $outdir $BASE_URL/PDS4_PDS_${version}.xsd

sed "s/REPLACE_ME/$version/g" src/build/resources/schema/PDS4_DISP_template.xsd > src/build/resources/schema/$version/PDS4_DISP_${version}.xsd

mvn versions:set-property -Dproperty=model-version -DnewVersion=$version

git add pom.xml $outdir
git commit -m "Updated IM version"
git push origin master

exit 0