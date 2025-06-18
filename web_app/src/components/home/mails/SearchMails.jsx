import { useAuth } from "../../../context/AuthProvider";
import { useState, useEffect } from "react";
import Loading from "../../loading/Loading";
import Mails from "./Mails";
import { useParams } from "react-router-dom";


const SearchMails = () => {
    const query = useParams().query;
    const [loading, setLoading] = useState(true);
    const [mails, setMails] = useState([]);
    const auth = useAuth();
    useEffect(() => {
        const fetchMails = async () => {
            const response = await fetch(`http://localhost:3000/api/search/${query}`, {
                headers: {
                    "Authorization": `${auth.token}`
                }
            });
            const data = await response.json();
            console.log(data);
            setMails(data);
            setLoading(false);
        }
        fetchMails();
    }, [auth.token, setMails, query]);
    if (loading) {
        return <Loading />;
    }
    return (
        <Mails mails={mails} setMails={setMails} />
    )
}

export default SearchMails;