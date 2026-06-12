import { useEffect, useState } from "react";

type Todo = { id: number; completed: boolean };

export const TodoW = () => {
    const [data, setData] = useState<Todo[]>([]);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const controller = new AbortController();

        (async () => {
            try {
                const res = await fetch("https://jsonplaceholder.typicode.com/todos/", {
                    signal: controller.signal,
                });

                if (!res.ok) throw new Error(String(res.status));

                const parsed = (await res.json()) as Todo[];
                setData(parsed);
            } catch (e) {
                if (e instanceof DOMException && e.name === "AbortError") return;
                setError(e instanceof Error ? e.message : "Unknown error");
            }
        })();

        return () => {
            controller.abort();
        };
    }, []);

    if (error) return <pre>{`Ошибка: ${error}`}</pre>;
    if (!data) return <pre>Загрузка...</pre>

    return (
        <>
            {data.map((t) => (
                <div key={t.id}>
                    {t.id} - {t.completed ? 'выполнено' : 'не выполнено'}
                </div>
            ))}
        </>
    )
}