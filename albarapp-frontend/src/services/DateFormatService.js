export default {
    formatDateText(dateTextIn) {
        var day = "";
        var month = "";
        var year = "";
        var format = false;
        var date = "";
        var dateFormatted = "";
        switch (dateTextIn.length) {
            case 8:
                if (!isNaN(dateTextIn)) {
                    // ddMMyyyy
                    day = dateTextIn.substring(0, 2);
                    month = dateTextIn.substring(2, 4);
                    year = dateTextIn.substring(4, 8);
                    date = year + "-" + month + "-" + day;
                    dateFormatted = day + "/" + month + "/" + year;
                    format = true;
                }
                break;
            case 10:
                if (
                    dateTextIn.substring(2, 3) === "/" &&
                    dateTextIn.substring(5, 6) === "/"
                ) {
                    // dd/MM/yyyy
                    day = dateTextIn.substring(0, 2);
                    month = dateTextIn.substring(3, 5);
                    year = dateTextIn.substring(6, 10);
                    date = year + "-" + month + "-" + day;
                    dateFormatted = day + "/" + month + "/" + year;
                }
                break;
            case 0:
                format = true;
                break;
            default:
                format = false;
        }
        return { date: date, dateFormatted: dateFormatted, format: format };
    },
    formatDatePick(dateTextIn) {
        var day = dateTextIn.substring(8, 10);
        var month = dateTextIn.substring(5, 7);
        var year = dateTextIn.substring(0, 4);
        return day + "/" + month + "/" + year;
    },
    datePickToTimestamp(dateTextIn) {
        var issuedDate = new Date();
        issuedDate.setDate(dateTextIn.substring(8, 10));
        issuedDate.setMonth(dateTextIn.substring(5, 7) - 1);
        issuedDate.setFullYear(dateTextIn.substring(0, 4));
        return issuedDate.getTime();
    }
}