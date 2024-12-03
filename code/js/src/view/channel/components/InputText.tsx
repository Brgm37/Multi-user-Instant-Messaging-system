import * as React from 'react';

export function InputText(
    {onSubmit}: { onSubmit: (text: string) => void }
): React.JSX.Element {
    const [text, setText] = React.useState<string>('')
    const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => setText(event.target.value)
    return (
        <div>
            <input
                type="text"
                value={text}
                onChange={handleChange}
                className={"bg-gray-700 text-white p-2 rounded"}
            />
            <button onClick={() => {
                onSubmit(text)
                setText('')
            }}>Submit</button>
        </div>
    )
}