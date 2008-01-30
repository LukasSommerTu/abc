#!/bin/sh -e

FILES="full_current.output failed_current.output \
      failed_current.xml passed_current.xml \
      skipped_current.xml"

cp failed.xml failed_current.xml
cp passed.xml passed_current.xml
cp skipped.xml skipped_current.xml
cp failed.output failed_current.output
cp full.output full_current.output
cvs commit -m"automatic update" $FILES
