default_pl=$1
default_pr=$2
car_package_strategy=$3
car_appear_strategy=$4
rsu_numbers=$5
list_rsu_xcoord=$6
list_rsu_ycoord=$7
list_rsu_zcoord=$8
exp_name=$9
optimizer=${10}
message_size=${11}

sed -i config.properties -e "s/\(default_pl=\).*/\1$default_pl/" \
       -e "s/\(default_pr=\).*/\1$default_pr/" \
       -e "s/\(car_package_strategy=\).*/\1$car_package_strategy/" \
       -e "s/\(car_appear_strategy=\).*/\1$car_appear_strategy/" \
       -e "s/\(rsu_numbers=\).*/\1$rsu_numbers/" \
       -e "s/\(list_rsu_xcoord=\).*/\1$list_rsu_xcoord/" \
       -e "s/\(list_rsu_ycoord=\).*/\1$list_rsu_ycoord/" \
       -e "s/\(list_rsu_zcoord=\).*/\1$list_rsu_zcoord/" \
       -e "s/\(exp_name=\).*/\1$exp_name/" \
       -e "s/\(optimizer=\).*/\1$optimizer/" \
       -e "s/\(message_size=\).*/\1$message_size/" \
       -e "s/\(message_cpu_cycle=\).*/\1$message_size/"
bash run.sh
